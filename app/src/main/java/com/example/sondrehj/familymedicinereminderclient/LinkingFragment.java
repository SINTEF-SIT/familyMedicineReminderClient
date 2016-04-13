package com.example.sondrehj.familymedicinereminderclient;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LinkingFragment.OnLinkingFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LinkingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LinkingFragment extends android.app.Fragment implements View.OnClickListener{

    private OnLinkingFragmentInteractionListener mListener;
    private ImageView statusIcon;

    public LinkingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LinkingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LinkingFragment newInstance() {
        LinkingFragment fragment = new LinkingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_linking, container, false);
        statusIcon = (ImageView) view.findViewById(R.id.status_icon);
        Button linkbutton = (Button) view.findViewById(R.id.link_button);
        linkbutton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.link_button:
                int color = Color.parseColor("#FF64DD17"); //The color u want
                statusIcon.setColorFilter(color);
                break;
            default:
                System.out.println("error happened in LinkingFragment: onClick Switch-case");
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLinkingFragmentInteractionListener) {
            mListener = (OnLinkingFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLinkingFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
