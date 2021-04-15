package com.example.projekatppandroid

//private var allInfo  = allImportantDeezerInfomation("469842",
//        "https://theappreciationengine.com/DeezerAuthenticator_Controller" ,
//        "basic_access,email,offline_access,manage_community",
//        "96ac181cddb8bc82fde2c64e44cb5804")

data class allImportantDeezerInfomation (
    private val appID : String = "469842",
    private val redirect : String = "https://theappreciationengine.com/DeezerAuthenticator_Controller",
    private val perms : String = "basic_access,email,offline_access,manage_library",
    private val secret : String = "96ac181cddb8bc82fde2c64e44cb5804"
) {
    // TODO: sakrij sve ove podatke! (vrv na githubu ima neka opcija)
    fun getappID() : String {
        return appID
    }
    fun getredirect() : String {
        return redirect
    }
    fun getperms() : String {
        return perms
    }
    fun getsecret() : String {
        return secret
    }
}