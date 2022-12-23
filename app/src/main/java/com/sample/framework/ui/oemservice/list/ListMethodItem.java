package com.sample.framework.ui.oemservice.list;


import com.airwatch.sdk.aidl.oem.OEMMethod;

/**
 * Created by STurner on 5/15/15.
 *
 * @author Stephen Turner <stephenturner265@air-watch.com>
 */
class ListMethodItem {
    final OEMMethod method;

    public ListMethodItem(OEMMethod method) {
        this.method = method;
    }

    @Override
    public String toString() {
        final int size = method.parameterNames.length;
        String name = size > 0 ? method.name + "(" : method.name + "()";
        for (int i = 0; i < size; i++) {
            name += method.parameterNames[i] + ":" + method.parameterTypes[i];
            if(i+1 < size){
                name += ", ";
            }else{
                name += ")";
            }
        }
        return name;
    }
}
