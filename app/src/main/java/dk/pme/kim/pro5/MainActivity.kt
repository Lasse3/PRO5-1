package dk.pme.kim.pro5

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun btnCollect(view: View)
    {
        if(id_collect.text.toString() == resources.getString(R.string.collectData))
        {
            id_collect.setText(resources.getString(R.string.collectingData))
        }

        else
        {
            id_collect.setText(resources.getString(R.string.collectData))
        }
    }

    fun btnTransfer(view: View)
    {
        if(id_transfer.text.toString().equals(resources.getString(R.string.transferData)))
        {
            id_transfer.setText(resources.getString(R.string.transferingData))
        }

        else {
            id_transfer.setText(resources.getString(R.string.transferData))
        }
    }
}