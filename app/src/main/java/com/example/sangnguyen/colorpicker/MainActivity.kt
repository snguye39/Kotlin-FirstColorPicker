package com.example.sangnguyen.colorpicker

import android.content.DialogInterface
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.*
import android.widget.*
import com.example.sangnguyen.colorpicker.R.id.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog.*
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    var red = 0
    var blue = 0
    var green = 0
    private val fileName = "Colors"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Set the Toolbar
        setSupportActionBar(findViewById(R.id.my_toolbar))

        // Get the values of the seekbars
        val seekRed = findViewById<SeekBar>(R.id.redBar)
        val seekBlue = findViewById<SeekBar>(R.id.blueBar)
        val seekGreen = findViewById<SeekBar>(R.id.greenBar)


        // Set the max value of the seekbars(default is 100)
        seekRed.max = 255
        seekBlue.max = 255
        seekGreen.max = 255



        // Get the value of the surfaceView
        val colorView = findViewById<SurfaceView>(R.id.surfaceView)

        // Set on change listener to get the value of the red seekBar and pass it to the surface view
        seekRed.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onStopTrackingTouch(SeekBar: SeekBar?) {}
            override fun onStartTrackingTouch(SeekBar: SeekBar?) {}
            override fun onProgressChanged(SeekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                redText.text = progress.toString()
                red = progress
                setSurfaceViewColor(red, blue, green)
                Log.d("red", red.toString())
            }
        })

        // Set on change listener to get the value of the blue seekBar and pass it to the surface view
        seekBlue.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onStopTrackingTouch(SeekBar: SeekBar?) {}
            override fun onStartTrackingTouch(SeekBar: SeekBar?) {}
            override fun onProgressChanged(SeekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                blueText.text = progress.toString()
                blue = progress
                setSurfaceViewColor(red, blue, green)
                Log.d("blue", blue.toString())
            }
        })

        // Set on change listener to get the value of the green seekBar and pass it to the surface view
        seekGreen.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(SeekBar: SeekBar?) {}
            override fun onStartTrackingTouch(SeekBar: SeekBar?) {}
            override fun onProgressChanged(SeekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                greenText.text = progress.toString()
                green = progress
                setSurfaceViewColor(red,blue,green)
                Log.d("green", green.toString())
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //add items to the action bar if it is present
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Handle action bar item clicks here.
        return when (item.itemId) {
            R.id.action_save -> {
                showSaveDialog()
                return true
            }

            R.id.action_recall -> {
                showRecallSpinner()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun showRecallSpinner() {
        val colorsList = getColorsList()

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.recall_spinner, null) as View
        builder.setTitle("Recall a Color")
        var spinner = dialogView.findViewById<Spinner>(R.id.recall_spinner)
        val colorAdapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,
                colorsList)
        colorAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = colorAdapter

        builder.setPositiveButton("Recall") {dialog, which ->
            val toast = Toast.makeText(this, spinner.selectedItem.toString() + " loaded successfully",
                    Toast.LENGTH_LONG)
            toast.show()
            dialog.dismiss()
            recallColor(spinner.selectedItem.toString())
        }
        builder.setNegativeButton("Cancel") {dialog, which ->
            dialog.dismiss()
        }
        builder.setView(dialogView)
        builder.create()
        builder.show()
    }


    private fun getColorsList() : ArrayList<String> {
        val file = File(filesDir, fileName)
        val sc = Scanner(file)
        var colorsList = ArrayList<String>()
        try {
            var line: String
            while(sc.hasNext()) {
                line = sc.nextLine()
                if (line != null) {
                    var name = line.split(",")
                    colorsList.add(name[0])
                }
            }
        }
        catch(e: NullPointerException) {
            var toast = Toast.makeText(this, "No colors saved", Toast.LENGTH_SHORT)
            toast.show()
        }
        return colorsList
    }
    private fun showSaveDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null) as View
        dialogBuilder.setView(dialogView)

        val editText = dialogView.findViewById<EditText>(R.id.save_text)

        dialogBuilder.setCancelable(false)
        dialogBuilder.setTitle("Save")
        dialogBuilder.setMessage("Enter the name of your color")
        dialogBuilder.setPositiveButton("Save") {dialog, which ->
            if (editText.text.toString().trim().isEmpty()) {
                var toast = Toast.makeText(this, "File name cannot be blank", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.TOP,0,0)
                toast.show()
            }
            saveFile(editText.text.toString())

        }
        dialogBuilder.setNegativeButton("Cancel") {dialog, which ->
            dialog.dismiss()
        }
        dialogBuilder.create()
        dialogBuilder.show()


    }


    private fun recallColor(name: String) {
        val file = File(filesDir, "Colors")
        val sc = Scanner(file)
        var line: String
        var fileColor: List<String>
        var r = 0
        var b = 0
        var g = 0
        while (sc.hasNext()) {
            line = sc.nextLine()
            fileColor = line.split(",")
            if (name.equals(fileColor[0])) {
                r = fileColor[1].trim().toInt()
                redText.text = fileColor[1].trim()
                redBar.progress = r
                g = fileColor[2].trim().toInt()
                greenText.text = fileColor[2].trim()
                greenBar.progress = b
                b = fileColor[3].trim().toInt()
                blueText.text = fileColor[3].trim()
                blueBar.progress = g

            }
        }
        setSurfaceViewColor(r,b,g)

    }

    private fun saveFile(name: String) {
        val file = File(filesDir, "Colors")
        if (file.exists()){
            file.appendText("\n$name, $red, $blue, $green")
            var toast = Toast.makeText(this, "$name saved", Toast.LENGTH_SHORT)
            toast.show()
        }
        else {
            val out = file.printWriter()
            out.println("$name,$red,$blue,$green")
            out.close()
            var toast = Toast.makeText(this, "$name saved", Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    private fun setSurfaceViewColor(r: Int, b: Int, g: Int) {
        surfaceView.setBackgroundColor(Color.rgb(r,g,b))
    }



}
