import org.bmsk.lifemash.configureCoroutineAndroid
import org.bmsk.lifemash.configureKotest
import org.bmsk.lifemash.configureKotlinAndroid

plugins {
    id("com.android.library")
    id("lifemash.verify.detekt")
}

configureKotlinAndroid()
configureKotest()
configureCoroutineAndroid()
