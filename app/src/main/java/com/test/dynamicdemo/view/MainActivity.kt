package com.test.dynamicdemo.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.hbb20.CountryCodePicker
import com.test.dynamicdemo.R
import com.test.dynamicdemo.model.AnswerMast
import com.test.dynamicdemo.model.SurveyData
import com.test.dynamicdemo.util.snack
import com.test.dynamicdemo.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {
    /** Koin is used as Dependency Injector**/
    private val viewModel by inject<MainViewModel>()
    private val fieldOrderedArray = mutableListOf<SurveyData>()
    private val l by lazy { LinearLayout(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.getApiData()
        observeFailureMsg()
        observeResponseData()
        clickActionOnPrivacyPolicy()
    }


    private fun observeFailureMsg() {
        viewModel.getFailureMessage().observe(this, Observer {
            container.snack(it!!, Snackbar.LENGTH_LONG)
        })
    }


    private fun observeResponseData() {
        viewModel.getResponseData().observe(this, Observer {
            sortResponseArray(it.surveyData)
        })
    }


    private fun sortResponseArray(it: Array<SurveyData>) {
        fieldOrderedArray.clear()
        cleanViews()
        fieldOrderedArray.addAll(it)
        fieldOrderedArray.sortBy { it.qSortOrder }
        addViews(fieldOrderedArray)
    }


    private fun addViews(fieldOrderedArray: MutableList<SurveyData>) {
        for (currentView in fieldOrderedArray) {
            if (currentView.controlType.contains("Textbox", true)) {
                if (currentView.question.contains("Mobile Number", true)) {
                    setupMobileNo(currentView)
                } else {
                    setupTextBox(currentView)
                }
            } else if (currentView.controlType.contains("Dropdown", true)) {
                setupSingleDropDown(currentView)
            }

        }
    }


    private fun cleanViews() {
        if (verticalLayout.childCount > 0) {
            verticalLayout.removeAllViews()
        }
        if (l.childCount > 0) {
            l.removeAllViews()
        }
        if (horizontalLayout.childCount > 0) {
            horizontalLayout.removeAllViews()
        }

    }


    private fun setupMobileNo(currentView: SurveyData) {
        l.orientation = LinearLayout.HORIZONTAL
        l.layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val countryCode = CountryCodePicker(this)
        val pramsTV = verticalLayout.layoutParams as (LinearLayout.LayoutParams)
        val pramsEV = horizontalLayout.layoutParams as (LinearLayout.LayoutParams)
        val prams = horizontalLayout.layoutParams as (LinearLayout.LayoutParams)
        val tv = TextView(this)
        val ev = EditText(this)

        when (currentView.keyboardType) {
            "Qwerty" ->
                ev.inputType = InputType.TYPE_CLASS_TEXT
            "number" ->
                ev.inputType = InputType.TYPE_CLASS_PHONE

        }

        countryCode.background = getDrawable(R.drawable.edittext_bg)
        pramsTV.setMargins(20, 50, 20, 0)
        prams.setMargins(20, 20, 20, 0)

        tv.text = currentView.question
        pramsEV.weight = 1f
        ev.background = getDrawable(R.drawable.edittext_bg)
        countryCode.setPadding(6, 0, 0, 6)
        ev.setPadding(20, 24, 20, 24)
        ev.tag = currentView.qid
        countryCode.setCountryForPhoneCode(971)
        countryCode.layoutParams = prams
        ev.layoutParams = pramsEV
        tv.layoutParams = pramsTV
        verticalLayout.addView(tv)
        l.addView(countryCode)
        l.addView(ev)
        verticalLayout.addView(l)

    }


    private fun setupTextBox(currentView: SurveyData) {
        val pramsTV = verticalLayout.layoutParams as (LinearLayout.LayoutParams)
        val pramsEV = verticalLayout.layoutParams as (LinearLayout.LayoutParams)
        val tv = TextView(this)
        val ev = EditText(this)

        pramsTV.setMargins(20, 50, 20, 0)
        pramsEV.setMargins(20, 20, 20, 0)

        tv.text = currentView.question
        ev.background = getDrawable(R.drawable.edittext_bg)

        when (currentView.keyboardType) {
            "Qwerty" ->
                ev.inputType = InputType.TYPE_CLASS_TEXT
            "number" ->
                ev.inputType = InputType.TYPE_CLASS_PHONE

        }

        ev.setPadding(20, 24, 20, 24)
        tv.layoutParams = pramsTV
        ev.layoutParams = pramsEV
        ev.tag = currentView.qid
        verticalLayout.addView(tv)
        verticalLayout.addView(ev)
    }


    private fun setupSingleDropDown(currentView: SurveyData) {
        val pramsTV = verticalLayout.layoutParams as (LinearLayout.LayoutParams)
        val pramsSpinner = verticalLayout.layoutParams as (LinearLayout.LayoutParams)
        val tv = TextView(this)
        val spinner = Spinner(this)
        val answerArray = mutableListOf<AnswerMast>()
        val spinnerArray = mutableListOf<String>()

        pramsTV.setMargins(20, 50, 20, 0)
        pramsSpinner.setMargins(20, 20, 20, 0)

        tv.text = currentView.question
        answerArray.addAll(currentView.answerMast)
        answerArray.sortBy { it.aSortOrder }
        spinnerArray.add("Tap to select")

        for (data in answerArray) {
            spinnerArray.add(data.answer)
        }

        val spinnerAdapter =
            ArrayAdapter<String>(
                this,
                R.layout.simple_spinner_drop_down, spinnerArray
            )

        spinner.adapter = spinnerAdapter
        spinner.background = getDrawable(R.drawable.edittext_bg)
        tv.layoutParams = pramsTV
        spinner.layoutParams = pramsSpinner
        spinner.tag = currentView.qid
        verticalLayout.addView(tv)
        verticalLayout.addView(spinner)
    }


    fun submitAction(view: View) {
        for (count in 0 until verticalLayout.childCount) {
            validator(verticalLayout.getChildAt(count))
        }
    }


    private fun validator(childAt: View) {
        when (childAt) {
            is EditText -> {
                validateEditText(childAt)
            }
            is LinearLayout -> {
                for (view in childAt) {
                    if (view is EditText) {
                        validateEditText(view)
                    }
                }
            }
            is Spinner -> {
                validateSpinner(childAt)
            }
        }
    }


    private fun validateSpinner(childAt: Spinner) {
        var validatorMessage = ""
        var isValidationRequired = false
        for (data in fieldOrderedArray) {
            if (data.qid == childAt.tag) {
                validatorMessage = data.requiredValidatorMessage
                isValidationRequired = data.requiredValidator.contains("y", true)
            }
        }
        if (!isValidationRequired) {
            return
        }
        if (childAt.selectedItem.toString().contains("Tap to select", true)) {
            container.snack(validatorMessage, Snackbar.LENGTH_LONG)
        }
    }


    private fun validateEditText(childAt: EditText) {
        var validatorPattern: Pattern? = null
        var validatorMessage = ""
        var isValidationRequired = false
        for (data in fieldOrderedArray) {
            if (data.qid == childAt.tag) {
                validatorPattern = Pattern.compile(data.regularExpression)
                validatorMessage = data.requiredValidatorMessage
                isValidationRequired = data.requiredValidator.contains("y", true)
            }
        }

        if (!isValidationRequired) {
            return
        }
        if ((!validatorPattern?.let {
                it.matcher(childAt.text).matches()
            }!!) || (childAt.text.isNullOrEmpty())) {
            childAt.error = validatorMessage
        }
    }


    private fun clickActionOnPrivacyPolicy() {
        val text = "I have read and agree to Privacy Policy"
        val ss = SpannableString(text)
        val privacyPolicy: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openUrl()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        ss.setSpan(privacyPolicy, 25, 39, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        privacyPolicyTV.text = ss
        privacyPolicyTV.movementMethod = LinkMovementMethod.getInstance()
    }


    private fun openUrl() {
        val url = "https://itware-me.com/"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        val title = "Select a browser"
        val chooser = Intent.createChooser(intent, title)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(chooser)
        }
    }

}