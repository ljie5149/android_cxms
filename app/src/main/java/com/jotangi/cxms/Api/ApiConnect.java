package com.jotangi.cxms.Api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.jotangi.cxms.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import android.os.Handler;
import android.os.Looper;

public class ApiConnect {

    private String TAG = ApiConnect.class.getSimpleName() + "(TAG)";

    private ApiConnect.resultListener listener;

    public interface resultListener {
        void onSuccess(String message);

        void onFailure(String task, String message);
    }

    private String runTask = "";


    public void getWeather(
            String adminArea,
            String timeFrom,
            String timeTo,
            resultListener listener
    ) {

        this.listener = listener;
        runTask = ApiConstant.TASK_weather;

        String url = ApiConstant.WEATHER_URL + adminArea
                + ApiConstant.ITEM_TIMEFROM + timeFrom
                + ApiConstant.ITEM_TIMETO + timeTo;
        Log.d(TAG, "URL: " + url);
        Log.d(TAG, "adminArea: " + adminArea);
        Log.d(TAG, "timeFrom: " + timeFrom);
        Log.d(TAG, "timeTo: " + timeTo);

        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();

        okClinet(request);
    }

    // 足壓量測
    public void getDataByMobile(
            String tel,
            resultListener listener
    ) {

        this.listener = listener;
        runTask = ApiConstant.TASK_fpm;

        String url = ApiConstant.ASIAFOOT_URL + ApiConstant.get_data_by_mobile;
        Log.d(TAG, "URL: " + url);

        JSONObject object = new JSONObject();
        try {
            object.put("mobile", tel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "object: " + object);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(object.toString(), mediaType);

        runExecute(url, requestBody);
    }

    // 門診摘要
    public void getMedicalRecordSummary2(
            String member_pid,
            String member_pwd,
            String time_start,
            String time_end,
            resultListener listener
    ) {

        this.listener = listener;

        String url = "https://clinic.healthme.com.tw/medicalec/api2/getrecdata.php";
        Log.d(TAG, "URL: " + url);
        runTask = ApiConstant.TASK_summary;

        JSONObject object = new JSONObject();
        try {
            object.put("member_pid", member_pid);
            object.put("member_pwd", member_pwd);
            object.put("startdate" , time_start);
            object.put("enddate"   , time_end);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "object: " + object);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(object.toString(), mediaType);

        runExecuteWithBearerToken(url, requestBody);
    }

    // 門診摘要
    public void getMedicalRecordSummary(
            String phone,
            String time_start,
            String time_end,
            resultListener listener
    ) {

        this.listener = listener;

        String url = "https://cloudeep.healthme.com.tw/api/v1/report/search";
        Log.d(TAG, "URL: " + url);
        runTask = ApiConstant.TASK_summary;

        JSONObject object = new JSONObject();
        try {
            object.put("vender"     , "minsheng");
            object.put("phone"      , phone);
            object.put("source"     , "checkup");
            object.put("time_start" , time_start);
            object.put("time_end"   , time_end);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "object: " + object);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(object.toString(), mediaType);

        runExecuteWithBearerToken(url, requestBody);
    }

    private void okClinet(Request request) {

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, AppUtils.logTitle("連線失敗"));
                e.printStackTrace();
                listener.onFailure(runTask, "連線失敗");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();

                if (responseBody != null) {
                    String body = responseBody.string();
                    Log.d(TAG, runTask + " body: " + body);

                    processBody(body);
                } else {
                    listener.onFailure(runTask, "無回傳資料");
                }
            }
        });
    }

    private void runExecute(String url, RequestBody requestBody) {

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        String body;

        try {
            Response response = new OkHttpClient().newCall(request).execute();

            if (response.isSuccessful()) {

                ResponseBody responseBody = response.body();

                if (responseBody != null) {
                    body = responseBody.string();
                    Log.w(TAG, runTask + " - body: " + body);

                    processBody(body);
                } else {

                    listener.onFailure(runTask, "無回傳資料");
                }

            } else {

                listener.onFailure(runTask, "連線失敗");
            }

        } catch (IOException e) {
            e.printStackTrace();
            listener.onFailure(runTask, "意外錯誤");
        }
    }

    private void runExecuteWithBearerToken(String url, RequestBody requestBody) {

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + "7f2b8c91-3a65-4e72-9d58-f04a127b3e9a")
                .post(requestBody)
                .build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                // 回到主執行緒回報錯誤
                new Handler(Looper.getMainLooper()).post(() -> {
                    listener.onFailure(runTask, "jacky 意外錯誤: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        String body = responseBody.string();

                        // 回到主執行緒處理結果
                        new Handler(Looper.getMainLooper()).post(() -> {
                            Log.w(TAG + " jacky", runTask + " - body: " + body);
                            processBody(body);
                        });

                    } else {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            listener.onFailure(runTask, "jacky 無回傳資料");
                        });
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        listener.onFailure(runTask, "jacky 連線失敗: " + response.code());
                    });
                }
            }
        });
    }


    private void processBody(String body) {

        switch (runTask) {
            case ApiConstant.TASK_weather:
                taskWeather_getWeather(body);
                break;
            case ApiConstant.TASK_fpm:
                taskFpm_getDataByMobile(body);
                break;
            case ApiConstant.TASK_summary:
                taskFpm_getSummary(body);
        }
    }

    private void taskWeather_getWeather(String body) {

        try {
            JSONObject jsonObject = new JSONObject(body);
            String success = jsonObject.getString("success");

            if ("true".equals(success)) {
                JSONArray locations = jsonObject
                        .getJSONObject("records")
                        .getJSONArray("locations").getJSONObject(0)
                        .getJSONArray("location").getJSONObject(0)
                        .getJSONArray("weatherElement");
                listener.onSuccess(locations.toString());
            } else {
                listener.onFailure("失敗", "連線失敗");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFailure(runTask, "回傳欄位不存在");
        }
    }

    private void taskFpm_getDataByMobile(String body) {

        try {
            JSONObject jsonObject = new JSONObject(body);
            String resultCode = jsonObject.getString("resultCode");

            if ("200".equals(resultCode)) {
                String events = jsonObject.getString("events");
                listener.onSuccess(events);
            } else {
                listener.onFailure("失敗", resultCode);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFailure(runTask, "回傳欄位不存在");
        }
    }
    private void taskFpm_getSummary(String body) {

        try {Object json = new JSONTokener(body).nextValue();

            if (json instanceof JSONArray) {
                // 是 JSONArray
                JSONArray jsonArray = (JSONArray) json;
                Log.d("Json判斷", "這是一個 JSON 陣列，長度: " + jsonArray.length());
                listener.onSuccess(body);

            } else if (json instanceof JSONObject) {
                // 是 JSONObject
                JSONObject jsonObject = (JSONObject) json;
                Log.d("Json判斷", "這是一個 JSON 物件，內容: " + jsonObject.toString());
                listener.onSuccess(body);

            } else {
                Log.e("Json判斷", "不是合法的 JSON 格式");
                listener.onFailure("失敗", "不是合法的 JSON 格式");
            }

        } catch (JSONException e) {
            Log.e("Json錯誤", "解析 JSON 發生錯誤: " + e.getMessage());
            listener.onSuccess("解析 JSON 發生錯誤: " + e.getMessage());
        }
    }
}
