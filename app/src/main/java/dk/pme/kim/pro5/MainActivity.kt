package dk.pme.kim.pro5

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var counterStepDetector=0


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check whether we're recreating a previously destroyed instance
        //update the value if we are, otherwise, just initialize it to 0.
        counterStepDetector = savedInstanceState?.getInt("STATE_STEP_DETECTOR") ?: 0

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
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
        outState.putInt("STATE_STEP_DETECTOR", counterStepDetector)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when(event?.sensor?.type){
            Sensor.TYPE_STEP_DETECTOR-> {
                counterStepDetector+= 1
                stepDetectorTxt.text = counterStepDetector.toString()
            }
        }

    fun btnCollect(view: View)
    {
        if(id_collect.text.toString() == resources.getString(R.string.collectData))
        {
            id_collect.text = resources.getString(R.string.collectingData)
        }

        else
        {
            id_collect.text = resources.getString(R.string.collectData)
        }
    }

    fun btnTransfer(view: View)
    {
        if(id_transfer.text.toString().equals(resources.getString(R.string.transferData)))
        {
            id_transfer.text = resources.getString(R.string.transferingData)
        }

        else {
            id_transfer.text = resources.getString(R.string.transferData)
        }
    }
}
}