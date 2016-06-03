package com.san.emenu.chowmein.localdb;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.san.emenu.chowmein.app.AppController;
import com.san.emenu.chowmein.bean.CategoryBean;
import com.san.emenu.chowmein.bean.IngredeintsBean;
import com.san.emenu.chowmein.bean.ItemsBean;
import com.san.emenu.chowmein.bean.SubCatBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by SANTECH on 03/02/2016.
 */
public class DBUtils {

    private static SQLiteDatabase db;
    private static String DB_PATH = "/sdcard/sanemenuch/";
    private final static String DB_NAME = "emenu.db";

    private final static String categorytbl = "category";
    private final static String subcattbl = "subcategory";
    private final static String itemstbl = "items";
    private final static String ingredientstbl = "ingredients";
    static int count;

    public static void storeCategories(List<CategoryBean> catList) {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);
        try {
            for (int i = 0; i < catList.size(); i++) {
                ContentValues cvInsert = new ContentValues();
                cvInsert.put("CAT_ID", catList.get(i).getRcatid());
                cvInsert.put("CAT_NAME", catList.get(i).getRcatname());
                cvInsert.put("SECTION_ID", catList.get(i).getSectionid());

                db.insert(categorytbl, null, cvInsert);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return;

    }

    public static void storesubcategories(List<SubCatBean> subcatList) {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

        Cursor data = null;

        try {

            for (int i = 0; i < subcatList.size(); i++) {
                try {
                    ContentValues cvInsert = new ContentValues();
                    cvInsert.put("RSCAT_ID", subcatList.get(i).getSccode());
                    cvInsert.put("RSCAT_NAME", subcatList.get(i).getScname());
                    cvInsert.put("RC_ID", subcatList.get(i).getRcid());
                    cvInsert.put("R_STATUS", subcatList.get(i).getRstatus());
                    cvInsert.put("STAT", subcatList.get(i).getStat());

                    /*Drawable d = sublst.get(i).getImgidnew();
                    Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bitmapdata = stream.toByteArray();
                    cvInsert.put("Rest_IMG", bitmapdata);*/

                    db.insert(subcattbl, null, cvInsert);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return;
    }

    public static void storeAllItems(List<ItemsBean> itemsList) {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);
        try {
            try {
                for (int i = 0; i < itemsList.size(); i++) {

                    ContentValues cvInsert = new ContentValues();
                    cvInsert.put("RITEM_ID", itemsList.get(i).getItemid());
                    cvInsert.put("RITEM_NAME", itemsList.get(i).getItemname());
                    cvInsert.put("RITEM_PRICE", itemsList.get(i).getItemprice());
                    cvInsert.put("RITEM_DESC", itemsList.get(i).getIngrdients());
                    cvInsert.put("RITEM_PRETIME", itemsList.get(i)
                            .getPretime());
                    cvInsert.put("RITEM_CODE", itemsList.get(i).getItemcode());
                    cvInsert.put("R_STATUS", itemsList.get(i).getRstatus());
                    cvInsert.put("RSCAT_ID", itemsList.get(i)
                            .getRscatid());
                    cvInsert.put("RRATING", itemsList.get(i).getRating());

                    db.insert(itemstbl, null, cvInsert);
                }
            } catch (Exception e) {

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return;
    }

    public static void storeingredients(List<IngredeintsBean> ingrList) {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

        try {
            try {
                for (int i = 0; i < ingrList.size(); i++) {

                    ContentValues cvInsert = new ContentValues();
                    cvInsert.put("ING_ID", ingrList.get(i).getIng_id());
                    cvInsert.put("ING_NAME", ingrList.get(i).getIng_name());

                    db.insert(ingredientstbl, null, cvInsert);
                }
            } catch (Exception e) {

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return;
    }

    /*public static  List<CategoryBean> getcategory() {
    final List<CategoryBean> catList = new ArrayList<>();
    final   List<CategoryBean> catList2 = new ArrayList<>();
        String url = "http://192.168.0.102/chowmein/allcategory.php";
        //JsonObjectRequest jrq = new JsonObjectRequest(url);
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = 0;
                            try {
                                success = response.getInt("success");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (success == 1) {
                                JSONArray ja = response.getJSONArray("items");
                                for (int i = 0; i < ja.length(); i++) {
                                    CategoryBean catbean = new CategoryBean();
                                    JSONObject jobj = ja.getJSONObject(i);
                                    catbean.setRcatid(jobj.getString("RCid"));
                                    catbean.setRcatname(jobj.getString("RCname"));
                                    // catbean.setRcatstatus(jobj.getInt("Ritem_price"));
                                    catList.add(catbean);

                                }

                               // DBUtils.storeCategories(catList);
                                for(int i=0; i<catList.size(); i++){
                                    //Toast.makeText(, "Item : " +catList.get(i).getRcatname(), Toast.LENGTH_SHORT).show();
                                    Log.d("TAG", "Item : " +catList.get(i).getRcatname());
                                   // catList2.add()

                                }

                            } else if (success == 0) {
                               // Toast.makeText(LoginScreen.this, "No Items found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                //PD.dismiss();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
        return catList;
    }*/

    public static List<CategoryBean> getcategoryfromdb() {
        List<CategoryBean> categorylist = new ArrayList<>();

        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

        Cursor data = null;

        try {
            try {
                data = db.rawQuery(
                        "select CAT_NAME,CAT_ID from category WHERE SECTION_ID=1", null);

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (data.moveToFirst()) {
                int i = 0;
                do {
                    CategoryBean catbean = new CategoryBean();

                    String item_name = data.getString(data
                            .getColumnIndex("CAT_NAME"));
                    catbean.setRcatid(data.getString(data
                            .getColumnIndex("CAT_ID")));
                    catbean.setRcatname(item_name);
                    categorylist.add(catbean);

                } while (data.moveToNext());
            }
        } catch (Exception e) {
        } finally {
            db.close();
        }

        return categorylist;
    }

    public static List<SubCatBean> getsubcatfrmdb(String val) {

        List<SubCatBean> SubCategoryList = new ArrayList<SubCatBean>();
        // MODELSubCategory[] SubCategoryList = null;
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

        Cursor data = null;
        //   Drawable image = null;

        try {
            try {
                data = db.rawQuery("select * from subcategory WHERE RC_ID="
                        + "'" + val + "'"
                        + " GROUP BY RSCAT_NAME order by  RSCAT_ID", null);

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (data.moveToFirst()) {
                int i = 0;
                do {
                    SubCatBean SubCategory = new SubCatBean();
                    String id = data.getString(data
                            .getColumnIndex("RSCAT_ID"));
                    String name = data.getString(data
                            .getColumnIndex("RSCAT_NAME"));
                    String cid = data.getString(data
                            .getColumnIndex("RC_ID"));
                    SubCategory.setScatid(id);
                    SubCategory.setScname(name);
                    SubCategory.setRcid(cid);

                    SubCategoryList.add(SubCategory);
                    //
                } while (data.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            data.close();
        }
        return SubCategoryList;

    }

    public static List<ItemsBean> getitemsfrmdb(String subcatid) {
        List<ItemsBean> itemlist = new ArrayList<ItemsBean>();
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

        Cursor data = null;
        //  Drawable image = null;

        try {
            try {

                data = db.rawQuery("select * from items where R_STATUS = 0 AND RSCAT_ID=" + subcatid, null);
                /* data=db.query(SAMPLE_TABLE_NAME1,new
                 String[]{"RItem_id","RItem_name","RItem_price","RItem_rating","RItem_ptime","RItem_Des","RItem_status","RItem_MODE"},"subcat_id=? AND RItem_status=? "
				 ,new String[]{subcatid,"0"}, null, null, "RItem_name ASC  ");*/

            } catch (Exception e) {
                e.toString();
            }
            if (data.moveToFirst()) {
                int i = 0;
                do {
                    try {

                        ItemsBean itmbean = new ItemsBean();
                        String item_id = data.getString(data
                                .getColumnIndex("RITEM_ID"));
                        String item_name = data.getString(data
                                .getColumnIndex("RITEM_NAME"));
                        String item_price = data.getString(data
                                .getColumnIndex("RITEM_PRICE"));
                        String rating = data.getString(data
                                .getColumnIndex("RRATING"));
                        // Shrikant
                        // byte[] img
                        // =data.getBlob(data.getColumnIndex("RItem_IMG"));
                        String ptime = data.getString(data
                                .getColumnIndex("RITEM_PRETIME"));
                        String item_des = data.getString(data
                                .getColumnIndex("RITEM_DESC"));
                        String stat = data.getString(data
                                .getColumnIndex("R_STATUS"));
                       /* String mod = data.getString(data
                                .getColumnIndex("RItem_MODE"));*/
                        // image = new
                        // BitmapDrawable(BitmapFactory.decodeByteArray(img, 0,
                        // img.length));
                        itmbean.setItemid(item_id);
                        itmbean.setItemname(item_name);
                        itmbean.setItemprice(item_price);
                        itmbean.setRating(rating);
                        itmbean.setIngrdients(item_des);
                        // moditem.setImgidnew(image);
                        itmbean.setPretime(ptime);
                        itmbean.setRstatus(stat);
                        // itmbean.setItemMode(mod);
                        itemlist.add(itmbean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } while (data.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            data.close();
        }
        return itemlist;

    }

    public static int getCountRestCat() {
        count = 0;
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

        Cursor dataCount = null;

        try {
            dataCount = db.rawQuery("select count(CAT_ID) from "
                    + categorytbl, null);

            if (dataCount != null) {
                if (dataCount.moveToFirst()) {

                    count = dataCount.getInt(0);
                }
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return count;
    }

    public static void insertRestCategory(List<CategoryBean> catList) {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);
        try {
            for (int i = 0; i < catList.size(); i++) {
                ContentValues cvInsert = new ContentValues();
                // cvInsert.put("secid", catList.get(i).getSectionID());
                cvInsert.put("CAT_ID", catList.get(i).getRcatid());
                cvInsert.put("CAT_NAME", catList.get(i).getRcatname());
                cvInsert.put("SECTION_ID", catList.get(i).getSectionid());

                db.insert(categorytbl, null, cvInsert);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return;
    }

    public static void addNewRestCategory(List<CategoryBean> catStsList) {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);
        try {
            //for (int i = 0; i < catStsList.size(); i++) {
            ContentValues cvInsert = new ContentValues();
            //cvInsert.put("secid", catStsList.get(0).getSectionID());
            cvInsert.put("CAT_ID", catStsList.get(0).getRcatid());
            cvInsert.put("CAT_NAME", catStsList.get(0).getRcatname());
            cvInsert.put("SECTION_ID", catStsList.get(0).getRcatstatus());

            db.insert(categorytbl, null, cvInsert);
            //	}

        } catch (Exception e) {
        } finally {
            db.close();
        }
        return;
    }

    public static void editRestaurantCategory(List<CategoryBean> updtCatList) {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

        // List<MODELCategory> list=new ArrayList<MODELCategory>();
        try {
            for (int i = 0; i < updtCatList.size(); i++) {
                try {
                    ContentValues cvInsert = new ContentValues();
                    // cvInsert.put("secid", updtCatList.get(i).getSectionID());
                    cvInsert.put("CAT_ID", updtCatList.get(i).getRcatid());
                    cvInsert.put("CAT_NAME", updtCatList.get(i)
                            .getRcatname());
                    /*cvInsert.put("cat_sec", updtCatList.get(i)
                            .getCategoryStatus());*/

                    db.update(categorytbl, cvInsert, "CAT_ID = ?",
                            new String[]{updtCatList.get(i).getRcatid()});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return;
    }


    public static Boolean deleteRestCat(String name) {

        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

        try {

            db.delete(categorytbl, "CAT_NAME= ?", new String[]{name});
        } finally {
            db.close();
        }
        return null;

    }

    public static List<CategoryBean> getRestCategory() {

        List<CategoryBean> restCatList = new ArrayList<CategoryBean>();

        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

        Cursor data = null;

        try {
            try {
                data = db.rawQuery("select CAT_NAME from category", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (data.moveToFirst()) {
                do {
                    // SubCategoryList = new MODELSubCategory[data.getCount()];

                    CategoryBean categories = new CategoryBean();
                    // String secid = data.getString(data.getColumnIndex("secid"));
                    String catid = data
                            .getString(data.getColumnIndex("CAT_NAME"));
                    /*String catname = data.getString(data
                            .getColumnIndex("cat_name"));
                    String catsec = data.getString(data
                            .getColumnIndex("cat_sec"));*/

                    // categories.setCatRSid(secid);
                    categories.setRcatname(catid);
                   /* categories.setCategoryName(catname);
                    categories.setCategoryStatus(catsec);*/
                    restCatList.add(categories);
                    //
                } while (data.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            data.close();
        }

        return restCatList;
    }

    public static int getSubCatCount() {

        count = 0;
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

        Cursor dataCount = null;

        try {
            dataCount = db.rawQuery("select count(RSCAT_ID) from subcategory",
                    null);

            if (dataCount != null) {
                if (dataCount.moveToFirst()) {

                    count = dataCount.getInt(0);
                }
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return count;

    }

    public static Boolean insertAllRstSubCat(List<SubCatBean> sublst) {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

        Cursor data = null;

        try {

            for (int i = 0; i < sublst.size(); i++) {
                try {
                    ContentValues cvInsert = new ContentValues();
                    cvInsert.put("RSCAT_ID", sublst.get(i).getScatid());
                    cvInsert.put("RSCAT_NAME", sublst.get(i).getScname());
                    cvInsert.put("RC_ID", sublst.get(i).getRcid());
                    // cvInsert.put("Rest_MODE", "R");

                   /* Drawable d = sublst.get(i).getImgidnew();
                    Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bitmapdata = stream.toByteArray();
                    cvInsert.put("Rest_IMG", bitmapdata);*/

                    db.insert(subcattbl, null, cvInsert);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return null;
    }

    public static Boolean insertNewRestSubCat(List<SubCatBean> sublst) {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);
           // Toast.makeText()
        Log.d("",sublst.get(0).getScname());
        try {
           // for (int i = 0; i < sublst.size(); i++) {
                ContentValues cvInsert = new ContentValues();
                cvInsert.put("RSCAT_ID", sublst.get(0).getScatid());
                cvInsert.put("RSCAT_NAME", sublst.get(0).getScname());
                cvInsert.put("RC_ID", sublst.get(0).getRcid());
                cvInsert.put("R_STATUS", sublst.get(0).getRstatus());
                cvInsert.put("STAT", sublst.get(0).getStat());

           /* Drawable d = sublst.get(0).getImgidnew();
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();
            cvInsert.put("Rest_IMG", bitmapdata);*/

                db.insert(subcattbl, null, cvInsert);
          //  }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            db.close();
        }
        return null;
    }

    public static void editRestSubCat(List<SubCatBean> subCatlist) {

        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

        Cursor data = null;

        try {
            ContentValues cvInsert = new ContentValues();
            cvInsert.put("RSCAT_ID", subCatlist.get(0).getScatid());
            cvInsert.put("RSCAT_NAME", subCatlist.get(0).getScname());
            cvInsert.put("RC_ID", subCatlist.get(0).getRcid());
           /* cvInsert.put("Rest_MODE", "R");

            Drawable d = subCatlist.get(0).getImgidnew();
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();
            cvInsert.put("Rest_IMG", bitmapdata);
*/
            //db.insert(SAMPLE_TABLE_NAME3, null, cvInsert);
            db.update(subcattbl, cvInsert, "RSCAT_ID = ?", new String[]{subCatlist.get(0).getScatid()});


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return;

    }

    public static Boolean deleteRstSubCat(String scname) {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

        try {

            db.delete(subcattbl, "RSCAT_NAME = ?", new String[]{scname});
        } finally {
            db.close();
        }
        return null;
    }

    public static List<SubCatBean> getRestSubCatNmNId() {
        List<SubCatBean> SubCategoryList = new ArrayList<SubCatBean>();
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

        Cursor data = null;

        try {
            try {
                data = db.rawQuery("select RSCAT_NAME from subcategory", null);

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (data.moveToFirst()) {
                int i = 0;
                do {
                    // SubCategoryList = new MODELSubCategory[data.getCount()];
                    SubCatBean SubCategory = new SubCatBean();
                    String id = data.getString(data.getColumnIndex("RSCAT_NAME"));
                    //String name = data.getString(data.getColumnIndex("Rest_subname"));
                    SubCategory.setScname(id);
                    // SubCategory.setSubcategoryName(name);

                    SubCategoryList.add(SubCategory);
                    //
                } while (data.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            data.close();
        }
        return SubCategoryList;
    }

    public static int getRestItemCount() {

        count = 0;
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

        Cursor dataCount = null;

        try {
            dataCount = db.rawQuery("select count(RITEM_NAME) from "
                    + itemstbl, null);

            if (dataCount != null) {
                if (dataCount.moveToFirst()) {

                    count = dataCount.getInt(0);
                }
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return count;

    }

    public static Boolean insertRestItms(List<ItemsBean> itmlst) {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);


        try {
            try {
                for (int i = 0; i < itmlst.size(); i++) {

                    ContentValues cvInsert = new ContentValues();
                    cvInsert.put("RITEM_ID", itmlst.get(i).getItemid());
                    cvInsert.put("RITEM_NAME", itmlst.get(i).getItemname());
                    cvInsert.put("RITEM_PRICE", itmlst.get(i).getItemprice());
                    cvInsert.put("RITEM_DESC", itmlst.get(i).getIngrdients());
                    cvInsert.put("RITEM_PRETIME", itmlst.get(i)
                            .getPretime());
                    cvInsert.put("RITEM_CODE", itmlst.get(i).getItemcode());
                    cvInsert.put("R_STATUS", itmlst.get(i)
                            .getRstatus());
                    cvInsert.put("RSCAT_ID", itmlst.get(i)
                            .getRscatid());
                    cvInsert.put("RRATING", itmlst.get(i).getRating());

                    db.insert(itemstbl, null, cvInsert);
                }
            } catch (Exception e) {

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return null;
    }

    public static void editRestItem(List<ItemsBean> itmList) {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

        // List<MODELCategory> list=new ArrayList<MODELCategory>();
        try {
            for (int i = 0; i < itmList.size(); i++) {
                try {
                    ContentValues cvInsert = new ContentValues();
                    cvInsert.put("RITEM_ID", itmList.get(i).getItemid());
                    cvInsert.put("RITEM_NAME", itmList.get(i).getItemname());
                    cvInsert.put("RITEM_PRICE", itmList.get(i).getItemprice());
                    cvInsert.put("RITEM_DESC", itmList.get(i).getIngrdients());
                    cvInsert.put("RITEM_PRETIME", itmList.get(i).getPretime());
                    cvInsert.put("RITEM_CODE", itmList.get(i).getItemcode());
                    cvInsert.put("R_STATUS", itmList.get(i).getRstatus());
                    cvInsert.put("RSCAT_ID", itmList.get(i).getRscatid());
                    cvInsert.put("RRATING", itmList.get(i).getRating());

                    db.update(itemstbl, cvInsert, "RITEM_ID = ?", new String[]{String.valueOf(itmList.get(i).getItemid())});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return;
    }

    public static Boolean deleteRestItem(String itmname) {

        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

        try {

            db.delete(itemstbl, "RITEM_NAME = ?",
                    new String[]{itmname});
        } finally {
            db.close();
        }
        return null;
    }

    public static List<ItemsBean> getRestItems() {
        List<ItemsBean> rItemList = new ArrayList<ItemsBean>();
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

        Cursor data = null;
        Drawable image = null;

        try {
            try {

                data = db.rawQuery("select RITEM_NAME from items ",
                        null);

            } catch (Exception e) {
                e.toString();
            }
            if (data.moveToFirst()) {
                int i = 0;
                do {
                    ItemsBean moditem = new ItemsBean();
                    String item_name = data.getString(data
                            .getColumnIndex("RITEM_NAME"));
                    /*String item_name = data.getString(data
                            .getColumnIndex("RItem_name"));*/

                    moditem.setItemname(item_name);
                    //  moditem.setItemname(item_name);

                    rItemList.add(moditem);

                } while (data.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            data.close();
        }
        return rItemList;

    }

}

