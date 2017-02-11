package com.example.xiaomage.criminalintent;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

/**
 * Created by xiaomage on 2016/12/22.
 */

public class CrimeDeleteFragment extends DialogFragment {

    public interface SetDelete{
        void setDelete(boolean isDelete);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delete,null);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Delete")
                .setMessage("Sure to delete?")
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(getFragmentManager().findFragmentById(R.id.detail_fragment_container) == null){
                                    /*是手机则在CrimePagerActivity中设置删除flag
                                    * 再通过调用onBackPressed中的setResult返回结
                                    * 果给CrimeListFragment*/
                                    ((SetDelete)getActivity()).setDelete(true);
                                    getActivity().onBackPressed();
                                }else{
                                    /*是平板版则直接在CrimeListFragment中设置删除
                                    flag，再删除*/
                                    Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);
                                    ((CrimeListFragment)fragment).setDeleteCrime(true);
                                    Fragment crimeFragment = getFragmentManager().findFragmentById(R.id.detail_fragment_container);
                                    int index = ((CrimeFragment)crimeFragment).getPosition();
                                    CrimeLab.get(getActivity()).removeCrime(index);
                                    ((CrimeListFragment)fragment).updateUI();

                                    /*删除后界面的处理：一种是直接显示第一个Crime，
                                    * 一种是显示空白*/
                                    if(CrimeLab.get(getActivity()).getCrimes().size() != 0){
                                        Crime crime = CrimeLab.get(getActivity()).getCrimes().get(0);
                                        CrimeFragment crimeFragment1 = CrimeFragment.newInstance(crime.getId(),0);
                                        getFragmentManager().beginTransaction()
                                                .replace(R.id.detail_fragment_container,crimeFragment1).commit();
                                    }else{
                                        getFragmentManager().beginTransaction()
                                                .replace(R.id.detail_fragment_container,new EmptyFragment())
                                                .add(R.id.fragment_container,new CrimeCreateFragment())
                                                .commit();
                                    }
                                }
                                Toast.makeText(getActivity(),R.string.delete_success,Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(getFragmentManager().findFragmentById(R.id.detail_fragment_container) == null){
                                    ((SetDelete)getActivity()).setDelete(false);
                                }else{
                                    Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);
                                    ((CrimeListFragment)fragment).setDeleteCrime(false);
                                }
                            }
                        })
                .create();
    }

}
