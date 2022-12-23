package com.sample.framework.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.airwatch.mutualtls.ClientTLSCertificateStorageException
import com.airwatch.net.HttpGetMessage
import com.airwatch.net.HttpServerConnection
import com.airwatch.sdk.context.SDKContextManager
import com.airwatch.task.TaskQueue
import com.airwatch.util.IOUtils
import com.airwatch.util.Logger
import com.sample.airwatchsdk.R
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.UnrecoverableEntryException
import java.security.cert.CertificateException

class MutualTLSActivity : AppCompatActivity() {

    private lateinit var fileSelectButton: Button
    private lateinit var passwordText: EditText
    private lateinit var addEntry: Button
    private lateinit var hostText: EditText
    private lateinit var deleteEntry: Button
    private lateinit var urlEditText: EditText
    private lateinit var urlButton: Button
    private var fileUri: Uri? = null
    private lateinit var urlResponse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aw_mutual_tls)

        fileSelectButton = findViewById(R.id.file_select_button)
        fileSelectButton.setOnClickListener { performFileSearch() }
        passwordText = findViewById(R.id.file_password_editText)
        addEntry = findViewById(R.id.add_tls_entry)
        addEntry.setOnClickListener {
            val runnable = Runnable { addMutualTlsEntry() }
            TaskQueue.getInstance().post("MutualTLSTest", runnable)
        }

        hostText = findViewById(R.id.host_editText)
        deleteEntry = findViewById(R.id.delete_tls_entry)
        deleteEntry.setOnClickListener {
            val runnable = Runnable { deleteMutualTlsEntry() }
            TaskQueue.getInstance().post("MutualTLSTest", runnable)
        }
        urlEditText = findViewById(R.id.url_edit_text)
        urlButton = findViewById(R.id.hit_url_button)
        urlButton.setOnClickListener {
            val runnable = Runnable { hitUrl(urlEditText.text.toString()) }
            urlResponse.text = "Sending test message .."
            TaskQueue.getInstance().post("MutualTLSTest", runnable)
        }
        urlResponse = findViewById(R.id.url_repsonse_textView)
    }

    private fun deleteMutualTlsEntry() {
        val clientTlsStorage = SDKContextManager.getSDKContext().clientTLSAuthStorage
        try {
            clientTlsStorage.deleteEntry(hostText.text.toString())
            Snackbar.make(urlResponse, "Entry Removed", Snackbar.LENGTH_SHORT).show()
        } catch (e: ClientTLSCertificateStorageException) {
            Logger.e(TAG, "Exception while deleting mutual Tls entry")
            Snackbar.make(urlResponse, "Exception deleting entry: ${e.message}", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.type = "*/*"

        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int,
                                         resultData: Intent?) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                fileUri = resultData.data
                Logger.i(TAG, "Uri: " + fileUri!!.toString())
                val returnCursor = contentResolver.query(
                        fileUri!!, null, null, null, null)
                returnCursor!!.moveToFirst()
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                fileSelectButton.text = returnCursor.getString(nameIndex)
                IOUtils.closeQuietly(returnCursor)
            }
        }
    }

    private fun addMutualTlsEntry() {

        if (fileUri == null) {
            Toast.makeText(this, "No or invalid file", Toast.LENGTH_SHORT).show()
            return
        }

        val clientTlsStorage = SDKContextManager.getSDKContext().clientTLSAuthStorage
        var keyStore: KeyStore? = null
        var `is`: InputStream? = null
        try {
            keyStore = KeyStore.getInstance("PKCS12")
            var password = passwordText.text.toString()
            if (TextUtils.isEmpty(password)) {
                Logger.e(TAG, "password not entered for keystore file.")
                password = ""
            }
            `is` = contentResolver.openInputStream(fileUri!!)
            keyStore!!.load(`is`, password.toCharArray())

            val alias = keyStore.aliases().nextElement()

            if (keyStore.isKeyEntry(alias)) {
                val pKeyEntry = keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry
                clientTlsStorage.addEntry(hostText.text.toString(), pKeyEntry.privateKey,
                        pKeyEntry.certificateChain, null)
                Snackbar.make(urlResponse, "Key Entry Added", Snackbar.LENGTH_SHORT).show()
            } else {
                Logger.e(TAG, "Non key-entry with alias: $alias")
            }
            return
        } catch (e: Exception) {
            Snackbar.make(urlResponse, "Exception adding entry: ${e.message}", Snackbar.LENGTH_SHORT).show()
            Logger.e(TAG, "Exception while adding mutual Tls entry")
        } finally {
            IOUtils.closeQuietly(`is`)
        }
    }

    private fun hitUrl(urlString: String) {
        if (TextUtils.isEmpty(urlString)) {
            Logger.e(TAG, "Invalid Url")
            return
        }
        val testMessage = TestMessage(urlString)


        try {
            testMessage.send()
        } catch (e: MalformedURLException) {
            Logger.e(TAG, "Exception while sending test message", e)

        }

        runOnUiThread {
            val s = "${testMessage.responseStatusCode}"
            urlResponse.text = "Response $s"
        }
    }

    internal class TestMessage(var url: String) : HttpGetMessage("SampleApp") {

        override fun getServerAddress(): HttpServerConnection {
            if (!url.startsWith("http") && !url.startsWith("https")) {
                url = "https://$url"
            }
            return HttpServerConnection.parse(url, false)
        }
    }

    companion object {

        private val TAG = "MutualTlsActivity"
        private val READ_REQUEST_CODE = 42
    }
}