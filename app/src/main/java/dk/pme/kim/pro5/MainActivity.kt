/*
*   Author      : Kim Nielsen, Tobias Valbjørn, Jesper Hjort, Lasse Frederiksen
*   Date        : 29-Nov 2018
*   Description : Personal activity tracker app.
*
* */

package dk.pme.kim.pro5

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var counterStepDetector=0

    private var startTime   : Long=0
    private var endTime     : Long=0
    private var currentTime : Long=0
    private var elapsedTime : Long=0
    private var minutes     : Long=0
    private var seconds     : Long=0
    private var collectFlag : Boolean=false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check whether we're recreating a previously destroyed instance
        //update the value if we are, otherwise, just initialize it to 0.
        counterStepDetector = savedInstanceState?.getInt("STATE_STEP_DETECTOR") ?: 0
        startTime = savedInstanceState?.getLong("START_TIME") ?: SystemClock.elapsedRealtime()
        collectFlag= savedInstanceState?.getBoolean("COLLECT_FLAG") ?: false
        currentTime=SystemClock.elapsedRealtime()
        elapsedTime=currentTime-startTime
        main_elapsed_time.text=elapsedTime.toString()+"seconds"

        val mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val mStepDetector= mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        if(mStepDetector == null)
        {
            Toast.makeText(this, "No Step Detector sensor was found!",
                    Toast.LENGTH_LONG).show()
        }
        else
        {
            mSensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_UI)
        }

        stepDetectorTxt.text = counterStepDetector.toString()

        id_collect.setOnClickListener{
            if(!collectFlag)
            {
                id_collect.text = resources.getString(R.string.collectingData)
                collectFlag=true
            }
            else
            {
                id_collect.text = resources.getString(R.string.collectData)
                endTime=SystemClock.elapsedRealtime()
                collectFlag=false
                counterStepDetector=0
            }

            startTime = SystemClock.elapsedRealtime()
        }


        id_transfer.setOnClickListener {
            if(id_transfer.text.toString() == resources.getString(R.string.transferData))
            {
                id_transfer.text = resources.getString(R.string.transferingData)
            }

            else {
                id_transfer.text = resources.getString(R.string.transferData)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
        outState.putInt("STATE_STEP_DETECTOR", counterStepDetector)
        outState.putLong("START_TIME", startTime)
        outState.putBoolean("COLLECT_FLAG",collectFlag)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (collectFlag) {
            when (event?.sensor?.type) {
                Sensor.TYPE_STEP_DETECTOR -> {
                    counterStepDetector += 1
                    stepDetectorTxt.text = counterStepDetector.toString()
                    currentTime = SystemClock.elapsedRealtime()
                    elapsedTime = currentTime - startTime
                    minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime)
                    seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime)

                    main_elapsed_time.text = "${minutes} min,${seconds} sec"
                }
            }
        }
    }
}