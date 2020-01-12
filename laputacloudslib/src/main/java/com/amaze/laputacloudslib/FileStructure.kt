package com.amaze.laputacloudslib

import com.onedrive.sdk.extensions.Drive

abstract class AbstractFileStructureDriver

class OneDriveDriver(val drive: Drive) : AbstractFileStructureDriver()