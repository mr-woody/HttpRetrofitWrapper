package com.simple.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import com.cz.android.sample.api.RefCategory
import com.cz.android.sample.api.RefRegister
import com.woodys.http.RetrofitWrapperManager
import com.woodys.http.core.Call
import com.woodys.http.core.exception.HttpError
import com.simple.BaseSampleAppCompatActivity
import com.simple.R
import com.simple.net.callback.AnimCallback
import com.simple.net.protocol.CommonApiService
import com.simple.utils.ColorUtils
import kotlinx.android.synthetic.main.activity_general_http_view.*
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * 演示常规的post、get(配合公共请求头和参数)
 */
@RefCategory(title=R.string.general_category,desc = R.string.general_category_desc, priority = 1)
@RefRegister(title=R.string.general_http_sample,desc=R.string.general_http_sample_desc,category = R.string.general_category,priority = 1)
class GeneralHttpActivity : BaseSampleAppCompatActivity(){

    override fun onActivityCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_general_http_view)

        gridView.adapter = MyAdapter()
        gridView.setOnItemClickListener{ parent: AdapterView<*>, view: View, position: Int, id: Long ->
            val item : String?  = gridView.adapter.getItem(position) as String?

            if("POST" == item){ postRequestNetWork() }

            if("GET" == item){ getRequestNetWork() }
        }
    }


    fun postRequestNetWork() {

        val uid: String? = ""
        val token: String? = ""

        val headMap = HashMap<String,String>()
        headMap["Request-From"] = "spersonal"

        val requestBody = HashMap<String, String?>()
        requestBody["uid"] = uid
        requestBody["token"] = token

        RetrofitWrapperManager.create(CommonApiService::class.java)
                .getUserInfo(headMap,requestBody)
                .bind(this)
                .enqueue(object: AnimCallback<ResponseBody>(this) {
                    override fun onSuccess(call: Call<ResponseBody>, response: Response<ResponseBody>, t: ResponseBody) {
                        handleResponse(response,t)
                    }

                    override fun onError(call: Call<ResponseBody>, error: HttpError) {
                        handleError(call.request(),error)
                    }
                })

    }

    fun getRequestNetWork() {
        RetrofitWrapperManager.create(CommonApiService::class.java)
                .getNetWorkDataFormGirl()
                .bind(this)
                .enqueue(object: AnimCallback<ResponseBody>(this) {
                    override fun onSuccess(call: Call<ResponseBody>, response: Response<ResponseBody>, t: ResponseBody) {
                        handleResponse(response,t)
                    }

                    override fun onError(call: Call<ResponseBody>, error: HttpError) {
                        handleError(call.request(),error)
                    }
                })
    }


    private class MyAdapter : BaseAdapter() {
        var methods = arrayOf("GET",  "POST")
        override fun getCount(): Int {
            return methods.size
        }

        override fun getItem(position: Int): String {
            return methods.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var convertView: View? = convertView
            if (convertView == null) {
                convertView = TextView(parent?.context)
            }
            val textView = convertView as TextView
            textView.gravity = Gravity.CENTER
            textView.height = 200
            textView.text = getItem(position)
            textView.setTextColor(Color.WHITE)
            textView.textSize = 16f
            textView.setBackgroundColor(ColorUtils.randomColor())
            return textView
        }
    }

}
