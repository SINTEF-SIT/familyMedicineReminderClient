package com.example.sondrehj.familymedicinereminderclient;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LinkingFragment.OnLinkingFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LinkingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LinkingFragment extends android.app.Fragment{

    private OnLinkingFragmentInteractionListener mListener;

    @Bind(R.id.link_button) Button linkButton;
    @Bind(R.id.status_icon) ImageView statusIcon;
    @Bind(R.id.id_input_field_data) TextView idInputField;
    @Bind(R.id.status_feedback) TextView statusText;

    public LinkingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LinkingFragment.
     */
    public static LinkingFragment newInstance() {
        LinkingFragment fragment = new LinkingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_linking, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.link_button)
    public void tryToLink() {
        if (idInputField.getTextSize() == 5) {
            boolean isLinked = linkWithAccount(" ");
            if (isLinked) {
                int color = Color.parseColor("#FF64DD17"); //The color u want
                statusIcon.setColorFilter(color);
            } else {
                int color = Color.parseColor("#FF64DD17"); //The color u want
                statusIcon.setColorFilter(color);
            }
        } else {
            statusText.setText("Enter a 5-digit ID. Try again...");
            clearStatusTextAfterSeconds(3);
        }

    }

    public void clearStatusTextAfterSeconds(int i) {
        new CountDownTimer(i*1000,1000){

            @Override
            public void onTick(long miliseconds){}

            @Override
            public void onFinish(){
                statusText.setText("");
            }
        }.start();
    }

    public boolean linkWithAccount(String idToLinkWith){

        return true;
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

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
