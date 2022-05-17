package com.akzubarev.homedoctor.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.MedicationStats;
import com.akzubarev.homedoctor.databinding.FragmentQrBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private NavController navController;
    private ZXingScannerView mScannerView;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentQrBinding binding = FragmentQrBinding.inflate(inflater, container, false);
        mScannerView = binding.scanner;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 123);
        }
        return binding.getRoot();
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//    }
//

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
//        getJSON("4602193012653");
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(com.google.zxing.Result result) {

        Log.d("result", result.getText());
        Log.d("result", result.getBarcodeFormat().toString());

        getJSON(result.getText());
    }

    String medicationName = null;

    private void getJSON(String medicationID) {
        Thread thread = new Thread(() -> {
            try {
                String link = String.format("https://barcode-list.ru/barcode/RU/barcode-%s/Поиск.htm", medicationID);
                URL url = new URL(link);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();


                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);

                urlConnection.setDoOutput(true);
                urlConnection.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("<meta name=\"Keywords")) {
                        medicationName = line.substring(line.indexOf("content") + 9, line.length() - 3).split(",")[0];
                        Log.d("result", "med: " + medicationName);
                        break;
                    }
                    sb.append(line).append("\n");
                }
                br.close();
//                        Log.d("result", "JSON: " + sb.toString());


            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (medicationName != null) {
            DataHandler dataHandler = DataHandler.getInstance(getContext());
            dataHandler.getMedicationStats(medicationStats ->
                    dataHandler.getMedications(medications -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        Optional<MedicationStats> medStat = medicationStats.stream().filter
                                (med -> med.getName().equals(medicationName)).findFirst();
                        Optional<Medication> medication = medications.stream().filter
                                (med -> med.getName().equals(medicationName)).findFirst();

                        if (medStat.isPresent()) {
                            builder.setTitle(medStat.get().getName())
                                    .setMessage("Лекарство найдено в базе")
                                    .setPositiveButton("Открыть лекарство", (dialog, id) -> {
                                        String medID = medStat.get().getDBID();
                                        navController = NavHostFragment.findNavController(this);
                                        Bundle bundle = new Bundle();
                                        if (medication.isPresent()) {
                                            bundle.putString("Medication", medication.get().getDBID());
                                            bundle.putBoolean("Add", false);
                                        } else
                                            bundle.putBoolean("Add", true);

                                        bundle.putString("MedicationStat", medID);
                                        navController.navigate(R.id.MedicationFragment, bundle);
                                    })
                                    .setNegativeButton("Отмена", (dialog, id) -> {
                                        dialog.dismiss();
                                        mScannerView.resumeCameraPreview(this);
                                    });
                        } else {
                            builder.setTitle("Ошибка")
                                    .setMessage("Лекарство не найдено в базе, попробуйте еще раз")
                                    .setPositiveButton("Ок", (dialog, id) -> {
                                        dialog.dismiss();
                                        mScannerView.resumeCameraPreview(this);
                                    });
                        }
                        builder.show();
                    })
            );
        }
    }

}