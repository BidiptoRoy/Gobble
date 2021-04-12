package com.bidiptoroy.gobble.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.bidiptoroy.gobble.R


class ProfileFragment : Fragment() {
    lateinit var txtName : TextView
    lateinit var txtEmail : TextView
    lateinit var txtAddress : TextView
    lateinit var txtPhone : TextView
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        txtName=view.findViewById(R.id.displayName)
        txtAddress=view.findViewById(R.id.displayAddress)
        txtEmail=view.findViewById(R.id.displayEmail)
        txtPhone=view.findViewById(R.id.displayPhoneNumber)
        sharedPreferences = (activity as FragmentActivity).getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)

        txtName.text = sharedPreferences.getString("name","Your Name")
        txtAddress.text = sharedPreferences.getString("address","Your Address")
        txtEmail.text=sharedPreferences.getString("email","example@gmail.com")
        txtPhone.text = "+91-"+sharedPreferences.getString("mobile_number","244 1139")


        return view
    }


}