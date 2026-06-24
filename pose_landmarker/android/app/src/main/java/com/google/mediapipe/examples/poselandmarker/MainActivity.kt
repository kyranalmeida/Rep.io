
package com.google.mediapipe.examples.poselandmarker

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.mediapipe.examples.poselandmarker.databinding.ActivityMainBinding
import java.util.concurrent.CopyOnWriteArrayList


class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var activityMainBinding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var sensorManager: SensorManager
    private lateinit var rotationSensor: Sensor
    private var azimuthDegrees: Float = 0.0f
    private var pitchDegrees: Float = 0.0f
    private var rollDegrees: Float = 0.0f
    private lateinit var rotationTextView: TextView
     var Angle1: Int = 0
     var Angle2: Int = 0
    var HipAngle: Int = 0
    private var rep: Int = 0
    private var RepCount: Int = 0

    data class WorkoutSet(val exercise: Int, val reps: Int, val videoPath: String?)

    companion object {
        @JvmStatic
        private var View_selection: Int = 0
        private  var excerise_selection: Int = 0
        private var repCount: Float = 0f
        private var Start: Boolean = false
        private var lastVideoPath: String? = null
        private val pendingSets = CopyOnWriteArrayList<WorkoutSet>()

        @JvmStatic
        fun setexcerise_selection(value: Int) {
            excerise_selection = value
        }

        @JvmStatic
        fun getexcerise_selection(): Int {
            return excerise_selection
        }
        @JvmStatic
        fun setViewSelection(value: Int) {
            View_selection = value
        }

        @JvmStatic
        fun getViewSelection(): Int {
            return View_selection
        }

        @JvmStatic
        fun getRepCount(): Int {
            return repCount.toInt()
        }

        @JvmStatic
        fun resetRepCount() {
            repCount = 0f
        }

        @JvmStatic
        fun setLastVideoPath(path: String) {
            lastVideoPath = path
        }

        @JvmStatic
        fun getLastVideoPath(): String? {
            return lastVideoPath
        }

        @JvmStatic
        fun clearLastVideoPath() {
            lastVideoPath = null
        }
        @JvmStatic
        fun setrepCount(value: Float) {
            repCount = value
        }

        @JvmStatic
        fun getrepCount(): Float {
            return repCount
        }
        @JvmStatic
        fun setStart(value: Boolean) {
            Start = value
        }

        @JvmStatic
        fun getStart(): Boolean {
            return Start
        }

        @JvmStatic
        fun getPendingSets(): List<WorkoutSet> = pendingSets.toList()
        
        @JvmStatic
        fun clearPendingSets() = pendingSets.clear()

        @JvmStatic
        fun recordCompletedSet(path: String?) {
            val reps = getRepCount()
            if (reps > 0) {
                pendingSets.add(WorkoutSet(getexcerise_selection(), reps, path))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        rotationTextView = findViewById(R.id.textView6)

        // Start rotation detection when the app starts
        startDetection()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        activityMainBinding.navigation.setupWithNavController(navController)
        activityMainBinding.navigation.setOnNavigationItemReselectedListener {
            // Ignore the reselection
        }

        findViewById<View>(R.id.back_btn).setOnClickListener {
            showExitDialog()
        }
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to go back? Your progress would not be saved.")
            .setPositiveButton("Save Workout") { _, _ ->
                finish()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopDetection()
    }

    private fun startDetection() {
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun stopDetection() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            val rotationAngles = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, rotationAngles)

            azimuthDegrees = Math.toDegrees(rotationAngles[0].toDouble()).toFloat()
            pitchDegrees = Math.toDegrees(rotationAngles[1].toDouble()).toFloat()
            rollDegrees = Math.toDegrees(rotationAngles[2].toDouble()).toFloat()

            if(Start) {
                if (excerise_selection == 1) {
                    if (View_selection == 1) {
                        if (pitchDegrees > 0.00f && rollDegrees > 0) {
                            if (Angle1 <= 45) {
                                if (rep == 0) {
                                    repCount += .5f
                                    rep = 1
                                }
                            } else if (Angle1 >= 45) {
                                if (rep == 1) {
                                    repCount += 0.5f
                                    rep = 0
                                }
                            }
                            RepCount = repCount.toInt()
                        } else if (pitchDegrees < 0 && rollDegrees < 0) {
                            if (Angle1 >= 45) {
                                if (rep == 0) {
                                    repCount += .5f
                                    rep = 1
                                }
                            } else if (Angle1 <= 45) {
                                if (rep == 1) {
                                    repCount += 0.5f
                                    rep = 0
                                }
                            }
                            RepCount = repCount.toInt()
                        } else if (pitchDegrees > 0 && pitchDegrees < 5 && rollDegrees < 0) {
                            if (Angle1 >= 135) {
                                if (rep == 0) {
                                    repCount += .5f
                                    rep = 1
                                }
                            } else if (Angle1 <= 135) {
                                if (rep == 1) {
                                    repCount += 0.5f
                                    rep = 0
                                }
                            }
                            RepCount = repCount.toInt()
                        } else if (pitchDegrees > 10 && rollDegrees < 0) {
                            if (Angle1 <= 135) {
                                if (rep == 0) {
                                    repCount += .5f
                                    rep = 1
                                }
                            } else if (Angle1 >= 135) {
                                if (rep == 1) {
                                    repCount += 0.5f
                                    rep = 0
                                }
                            }
                            RepCount = repCount.toInt()
                        }
                        rotationTextView.text = "Rep $RepCount"
                    }
                } else if (excerise_selection == 2) {
                    if (View_selection == 1) {
                        if (pitchDegrees > 0.00f && rollDegrees > 0) {
                            if (HipAngle <= 45) {
                                if (rep == 0) {
                                    repCount += .5f
                                    rep = 1
                                }
                            } else if (HipAngle >= 45) {
                                if (rep == 1) {
                                    repCount += 0.5f
                                    rep = 0
                                }
                            }
                            RepCount = repCount.toInt()
                        } else if (pitchDegrees < 0 && rollDegrees < 0) {
                            if (HipAngle >= 45) {
                                if (rep == 0) {
                                    repCount += .5f
                                    rep = 1
                                }
                            } else if (HipAngle <= 45) {
                                if (rep == 1) {
                                    repCount += 0.5f
                                    rep = 0
                                }
                            }
                            RepCount = repCount.toInt()
                        } else if (pitchDegrees > 0 && pitchDegrees < 5 && rollDegrees < 0) {
                            if (HipAngle >= 135) {
                                if (rep == 0) {
                                    repCount += .5f
                                    rep = 1
                                }
                            } else if (HipAngle <= 135) {
                                if (rep == 1) {
                                    repCount += 0.5f
                                    rep = 0
                                }
                            }
                            RepCount = repCount.toInt()
                        } else if (pitchDegrees > 10 && rollDegrees < 0) {
                            if (HipAngle <= 135) {
                                if (rep == 0) {
                                    repCount += .5f
                                    rep = 1
                                }
                            } else if (HipAngle >= 135) {
                                if (rep == 1) {
                                    repCount += 0.5f
                                    rep = 0
                                }
                            }
                            RepCount = repCount.toInt()
                        }
                        rotationTextView.text = "Rep $RepCount"
                    }
                }
            }
        }
    }
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }

    override fun onBackPressed() {
        showExitDialog()
    }
    fun Start(view: View?) {
        setStart(!getStart())
        val b = view as Button
        if(Start) {
             b.text = "Stop"
            setrepCount(0f)
            viewModel.setRecording(true)
        } else {
            b.text = "Start"
            viewModel.setRecording(false)
        }
    }
}
