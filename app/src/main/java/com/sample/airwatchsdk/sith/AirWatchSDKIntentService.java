/*
 * AirWatch Android Software Development Kit
 * Copyright (c) 2017 AirWatch. All rights reserved.
 *
 * Unless required by applicable law, this Software Development Kit and sample
 * code is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. AirWatch expressly disclaims any and all
 * liability resulting from User's use of this Software Development Kit. Any
 * modification made to the SDK must have written approval by AirWatch.
 */

package com.sample.airwatchsdk.sith;

/**
 * This class is used to support sith app which package name is "com.sample.airwatchsdk.sample"
 * since when we start AirWatchSDKIntentService, we get service path from the combination of
 * packageName + service class name. So this file is needed if the package name is com.sample.airwatchsdk.sample.sith
 * .Otherwise, app will throw NoClassFound for "com.sample.airwatchsdk.sample.sith.AirWatchSDKIntentService"
 *
 */
public class AirWatchSDKIntentService extends com.sample.airwatchsdk.AirWatchSDKIntentService {

}
