package com.turahan.dev.user.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.turahan.dev.R

class IsiDonasi : BottomSheetDialogFragment() {

    private lateinit var cardBri: CardView
    private lateinit var cardBni: CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_isi_donasi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardBri = view.findViewById(R.id.card_bri)
        cardBni = view.findViewById(R.id.card_bni)




    }

}