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

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var counterStepDetector=0

    private var startTime   : Long=0
    private var endTime     : Long=0
    private var currentTime : Long=0
    private var elapsedTime : Long=0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check whether we're recreating a previously destroyed instance
        //update the value if we are, otherwise, just initialize it to 0.
        counterStepDetector = savedInstanceState?.getInt("STATE_STEP_DETECTOR") ?: 0
        startTime = savedInstanceState?.getLong("START_TIME") ?: 0
        currentTime=SystemClock.elapsedRealtime()
        elapsedTime=startTime-currentTime
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
            if(id_collect.text.toString() == resources.getString(R.string.collectData))
            {
                id_collect.text = resources.getString(R.string.collectingData)
            }

            else
            {
                id_collect.text = resources.getString(R.string.collectData)
                endTime=SystemClock.elapsedRealtime()/1000
            }

            startTime = SystemClock.elapsedRealtime()/1000
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
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when(event?.sensor?.type){
            Sensor.TYPE_STEP_DETECTOR-> {
                counterStepDetector+= 1
                stepDetectorTxt.text = counterStepDetector.toString()
                currentTime=SystemClock.elapsedRealtime()/1000
                elapsedTime=(currentTime-startTime)
                if(elapsedTime<60){
                    main_elapsed_time.text= "${elapsedTime} seconds"
                }
                if(elapsedTime in 60..119){
                    val minute: Float=elapsedTime/60.toFloat()
                    main_elapsed_time.text= "${minute} minute"
                }
                if(elapsedTime>=120){
                    val minutes: Float=elapsedTime/60.toFloat()
                    main_elapsed_time.text="${minutes} minutes"
                }



            }
        }
    }

}