package uscworkshops.akashdeep.com.tneandroidapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView

import org.json.JSONException
import org.json.JSONObject


import uscworkshops.akashdeep.com.tneandroidapp.R
import uscworkshops.akashdeep.com.tneandroidapp.helpers.PlaceInfoData

/**
 * Created by akash on 4/20/2018.
 */

class PlaceInfoFragment : Fragment() {

    private var mAddress: TextView? = null
    private var mPhone: TextView? = null
    private var mPricing: TextView? = null
    private var mGooglePage: TextView? = null
    private var mWebsite: TextView? = null
    private var mAddressLayout: LinearLayout? = null
    private var mPhoneLayout: LinearLayout? = null
    private var mPricingLayout: LinearLayout? = null
    private var mRatingLayout: RelativeLayout? = null
    private var mGooglePageLayout: LinearLayout? = null
    private var mWebsiteLayout: LinearLayout? = null
    private var mRatingBar: RatingBar? = null
    private var mInfoData: PlaceInfoData? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_placeinfo, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAddress = view!!.findViewById(R.id.address_view_text) as TextView
        mPhone = view.findViewById(R.id.phone_view_text) as TextView
        mPricing = view.findViewById(R.id.price_view_text) as TextView
        mGooglePage = view.findViewById(R.id.google_view_text) as TextView
        mWebsite = view.findViewById(R.id.web_view_text) as TextView
        mRatingBar = view.findViewById(R.id.rating_view_text) as RatingBar
        mAddressLayout = view.findViewById(R.id.address_layout) as LinearLayout
        mPhoneLayout = view.findViewById(R.id.phone_layout) as LinearLayout
        mPricingLayout = view.findViewById(R.id.price_level_ayout) as LinearLayout
        mRatingLayout = view.findViewById(R.id.rating_level_layout) as RelativeLayout
        mGooglePageLayout = view.findViewById(R.id.google_page_layout) as LinearLayout
        mWebsiteLayout = view.findViewById(R.id.website_layout) as LinearLayout
        mInfoData = PlaceInfoData.getInstance()
        populateInfoData()
    }

    private fun populateInfoData() {
        val placeResults = mInfoData!!.placeInfoResult
        try {
            val obj = JSONObject(placeResults)
            if (obj.has("result")) {
                val r1 = obj.getJSONObject("result")
                if (r1.has("formatted_address")) {
                    mAddressLayout!!.visibility = View.VISIBLE
                    val address = r1.getString("formatted_address")
                    mInfoData!!.address = address
                    mAddress!!.text = address
                } else {
                    mAddressLayout!!.visibility = View.GONE
                }
                if (r1.has("formatted_phone_number")) {
                    mPhoneLayout!!.visibility = View.VISIBLE
                    val phone = r1.getString("formatted_phone_number")
                    mInfoData!!.phoneNumber = phone
                    mPhone!!.text = phone
                } else {
                    mPhoneLayout!!.visibility = View.GONE
                }
                if (r1.has("rating")) {
                    mRatingLayout!!.visibility = View.VISIBLE
                    val rating = r1.getString("rating")
                    val r2 = java.lang.Float.valueOf(rating)!!
                    mInfoData!!.ratingLevel = rating
                    mRatingBar!!.rating = r2
                } else {
                    mRatingLayout!!.visibility = View.GONE
                }
                if (r1.has("url")) {
                    mGooglePageLayout!!.visibility = View.VISIBLE
                    val ur1 = r1.getString("url")
                    mInfoData!!.googlePage = ur1
                    mGooglePage!!.text = ur1
                } else {
                    mGooglePageLayout!!.visibility = View.GONE
                }
                if (r1.has("website")) {
                    mWebsiteLayout!!.visibility = View.VISIBLE
                    val web = r1.getString("website")
                    mInfoData!!.websiteUrl = web
                    mWebsite!!.text = web
                } else {
                    mWebsiteLayout!!.visibility = View.GONE
                }
                if (r1.has("price_level")) {
                    mPricing!!.visibility = View.VISIBLE
                    val pLevel = r1.getString("price_level")
                    val l1 = Integer.parseInt(pLevel)
                    var dollar = ""
                    for (i in 1..l1) {
                        dollar = "$dollar$"
                    }
                    mPricing!!.text = dollar
                    mInfoData!!.pricingLevel = dollar
                } else {
                    mPricingLayout!!.visibility = View.GONE
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    companion object {

        fun newInstance(): PlaceInfoFragment {
            return PlaceInfoFragment()
        }
    }
}
