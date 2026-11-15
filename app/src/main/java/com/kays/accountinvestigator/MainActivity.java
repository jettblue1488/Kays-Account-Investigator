package com.kays.accountinvestigator;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

public class MainActivity extends Activity {
  private static final int PICK = 17;
  private LinearLayout layout;
  private TextView results;
  private final String[][] sites = {
    {"Facebook","facebook.com"}, {"Instagram","instagram.com"}, {"TikTok","tiktok.com"},
    {"Snapchat","snapchat.com"}, {"Tinder","tinder.com"}, {"Bumble","bumble.com"},
    {"Hinge","hinge.co"}, {"Plenty of Fish","pof.com"}, {"Match","match.com"},
    {"OkCupid","okcupid.com"}, {"Discord","discord.com"}, {"Reddit","reddit.com"}
  };
  @Override public void onCreate(Bundle b) { super.onCreate(b);
    getWindow().setStatusBarColor(Color.BLACK);
    ScrollView scroll = new ScrollView(this); scroll.setBackgroundColor(Color.rgb(12,12,15));
    layout = new LinearLayout(this); layout.setOrientation(LinearLayout.VERTICAL); layout.setPadding(30,40,30,30);
    scroll.addView(layout); setContentView(scroll);
    TextView title = text("🕷 KAY'S ACCOUNT INVESTIGATOR", 23, Color.RED); title.setTypeface(null,Typeface.BOLD);
    text("Private email evidence scanner • Black Widow Edition",14,Color.LTGRAY);
    text("Choose an email export (.eml, .mbox or .txt). Only import mailboxes you own or are authorized to examine. No emails leave this device.",15,Color.WHITE);
    Button pick = new Button(this); pick.setText("SELECT EMAIL FILE"); pick.setTextColor(Color.WHITE); pick.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(170,0,25)));
    layout.addView(pick); pick.setOnClickListener(v -> { Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT); i.setType("*/*"); i.addCategory(Intent.CATEGORY_OPENABLE); startActivityForResult(i,PICK); });
    results = text("No files scanned yet.",16,Color.WHITE);
    Button history = new Button(this); history.setText("SHOW SAVED RESULTS"); layout.addView(history);
    history.setOnClickListener(v -> results.setText(getPreferences(0).getString("history","No saved results.")));
    text("A domain mention is a clue, not proof of an active account. Check the original message before drawing conclusions.",13,Color.LTGRAY);
  }
  private TextView text(String s,int size,int color) { TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); t.setPadding(0,12,0,18); layout.addView(t); return t; }
  @Override protected void onActivityResult(int req,int res,Intent data) { super.onActivityResult(req,res,data);
    if(req!=PICK || res!=RESULT_OK || data==null) return;
    try(InputStream in=getContentResolver().openInputStream(data.getData())) {
      if(in==null) throw new IOException("File unavailable");
      ByteArrayOutputStream out=new ByteArrayOutputStream(); byte[] buf=new byte[8192]; int n; int total=0;
      while((n=in.read(buf))!=-1){total+=n; if(total>12_000_000) throw new IOException("File exceeds 12 MB limit");out.write(buf,0,n);}
      String mail=out.toString("UTF-8"); String lower=mail.toLowerCase(Locale.ROOT);
      StringBuilder found=new StringBuilder("SCAN RESULTS\n\n"); int count=0;
      for(String[] site:sites){ Matcher m=Pattern.compile("(?i)(?:[a-z0-9._%+-]+@)?(?:[a-z0-9-]+\\.)*"+Pattern.quote(site[1])).matcher(mail);
        int hits=0; while(m.find() && hits<999) hits++;
        if(hits>0){ count++; found.append(site[0]).append(": ").append(hits).append(" domain mentions\n"); }
      }
      if(count==0)found.append("No matching platform domains found.");
      found.append("\n\nDomain matches alone do not confirm registration, ownership, or activity.");
      String result=found.toString(); results.setText(result);
      getPreferences(0).edit().putString("history",new Date().toString()+"\n"+result).apply();
    }catch(Exception e){results.setText("Unable to scan: "+e.getMessage());}
  }
}
