/*
*   Author      : Kim Nielsen, Tobias Valbjørn, Jesper Hjort, Lasse Frederiksen
*   Date        : 29-Nov 2018
*   Description : Personal activity tracker app.
*
* */

package dk.pme.kim.pro5

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), SensorEventListener {

    //	Array with permissions:
    private var permissionsRequired = arrayOf(
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    //	Values for uploading:
    private val filename = "data2.txt"
    private val mobilePath_upload = "/data/data/dk.pme.kim.pro5/files/"+
			filename
    private val firebasePath_upload = "Data/"+filename
    private var permFlag = false

    //	Firebase url and file to upload:
    private val url = "gs://storageexample-916c1.appspot.com"
    val file = Uri.fromFile(File(mobilePath_upload))

    //  File to be written/appended to:
    val fh = fileHandler()

    private var startSteps=0
    private var steps=0
    private var startTime  : Long=0
    private var endTime     : Long=0
    private var currentTime : Long=0
    private var elapsedTime : Long=0
    private var minutes     : Long=0
    private var seconds     : Long=0
    private var collectFlag : Boolean=false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        fun getInstances(){
            // Check whether we're recreating a previously destroyed instance
            //update the value if we are, otherwise, just initialize it to 0.
            startSteps =
					savedInstanceState?.getInt("STATE_STEP_DETECTOR") ?: 0
            startTime =
					savedInstanceState?.getLong("START_TIME") ?: 0
            collectFlag=
					savedInstanceState?.getBoolean("COLLECT_FLAG") ?: false

            permFlag=
                    savedInstanceState?.getBoolean("PERMISSION_FLAG") ?:
                    false
        }
        fun setupSensors(){
            val mSensorManager =
					getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val mStepDetector=
					mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

            if(mStepDetector == null)
            {
				toast("No Step Detector sensor was found!")
            }
            else
            {
                mSensorManager.registerListener(this, mStepDetector,
						SensorManager.SENSOR_DELAY_UI)
            }
        }

        fun init(){
            ActivityCompat.requestPermissions(this,
                    permissionsRequired, 123)

            getInstances()
            setupSensors()
            
            main_elapsed_time.text= "${elapsedTime}seconds"
            stepDetectorTxt.text = startSteps.toString()
        }

        fun fileWriter(){
            fh.appendDataFile(filename, steps.toString(),
                    applicationContext)

            fh.appendDataFile(filename, ", ",
                    applicationContext)

            fh.appendDataFile(filename, "${minutes}.${seconds}",
                    applicationContext)

            //22, 24.53, 500
            fh.appendDataFile(filename, "\n",
                    applicationContext)
        }

        fun setClickListeners(){
            id_collect.setOnClickListener{
                if(!collectFlag)
                {
                    startTime = SystemClock.elapsedRealtime()
                    id_collect.text =
                            resources.getString(R.string.collectingData)
                    collectFlag=true
                }
                else
                {
                    steps = startSteps
                    startSteps=0
                    id_collect.text = resources.getString(R.string.collectData)
                    endTime=SystemClock.elapsedRealtime()
                    collectFlag=false
                }
            }

            id_transfer.setOnClickListener {
                fileWriter()

				if(permFlag){
					uploadFile(url, firebasePath_upload, file)
				}
                steps = 0
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        setClickListeners()
    }

    private fun putVariables(outState: Bundle){
        outState.putInt("STATE_STEP_DETECTOR", startSteps)
        outState.putLong("START_TIME", startTime)
        outState.putBoolean("COLLECT_FLAG",collectFlag)
        outState.putBoolean("PERMISSION_FLAG",permFlag)
        outState.putInt("START_STEPS", startSteps)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
        putVariables(outState)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun getElapsedTime(){
        currentTime=SystemClock.elapsedRealtime()
        elapsedTime=currentTime-startTime
        minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime)
        seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime)
    }

    private fun updateActivityInfo(){
        startSteps += 1
        stepDetectorTxt.text = startSteps.toString()
        getElapsedTime()
        main_elapsed_time.text = "${minutes} min,${seconds} sec"
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (collectFlag) {
            when (event?.sensor?.type) {
                Sensor.TYPE_STEP_DETECTOR -> {
                    updateActivityInfo()
                }
            }
        }
    }

    //	Upload file:
    fun uploadFile(url : String, path : String, file : Uri){
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url)
        val fileRef = storageRef.child(path)

        //pBar_upload.visibility = View.VISIBLE
        //pBar_upload.progress = pBar_upload.min

        fileRef.putFile(file)
                .addOnSuccessListener {
                    //pBar_upload.progress = pBar_upload.max
                    //pBar_upload.visibility = View.GONE
					toast("Upload succesful!")
                }
                .addOnFailureListener {
                    Log.e("Upload_error_message", it.message)
                    Log.e("Upload_error_stacktrace",
							it.stackTrace.toString())
                    Log.e("Upload_error_cause", it.cause.toString())
					toast("Upload unsuccesful!")
                }
    }

	//	Run code if permissions are granted:
    override fun onRequestPermissionsResult(requestCode: Int,
											permissions: Array<out String>,
											grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 123){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED){
                permFlag = true
            }

            else{
                toast("Permissions need to be allowed...")
            }
        }
    }

	// Extension function to show toast message
	private fun Context.toast(message:String){
		Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
	}
}