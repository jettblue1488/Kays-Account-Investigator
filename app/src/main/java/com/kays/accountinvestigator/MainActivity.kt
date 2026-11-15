package com.kays.accountinvestigator

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : Activity() {
    private lateinit var output: TextView
    private val red = Color.rgb(210, 25, 49)
    private val platforms = mapOf(
        "Instagram" to listOf("instagram.com"),
        "Facebook" to listOf("facebookmail.com", "facebook.com"),
        "TikTok" to listOf("tiktok.com"),
        "Snapchat" to listOf("snapchat.com"),
        "Tinder" to listOf("tinder.com"),
        "Bumble" to listOf("bumble.com"),
        "Hinge" to listOf("hinge.co"),
        "Plenty of Fish" to listOf("pof.com", "plentyoffish.com")
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(30, 45, 30, 20); setBackgroundColor(Color.rgb(15,15,18)) }
        val title = TextView(this).apply { text = "🕷  KAY'S ACCOUNT INVESTIGATOR"; textSize = 23f; setTextColor(red); setTypeface(null, Typeface.BOLD) }
        root.addView(title)
        val intro = TextView(this).apply { text = "Private, on-device email evidence scanner. Import an email file you own or are authorized to examine. Results are clues, not proof of current account activity."; setTextColor(Color.WHITE); textSize = 15f; setPadding(0,24,0,24) }
        root.addView(intro)
        val button = Button(this).apply { text = "SELECT EMAIL FILE (.EML OR .TXT)"; setBackgroundTintList(android.content.res.ColorStateList.valueOf(red)); setTextColor(Color.WHITE); setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "*/*"; addCategory(Intent.CATEGORY_OPENABLE) }
            startActivityForResult(intent, 42)
        } }
        root.addView(button)
        output = TextView(this).apply { text = "No file scanned yet."; textSize = 16f; setTextColor(Color.WHITE); setPadding(0,24,0,0) }
        val scroll = ScrollView(this)
        scroll.addView(output)
        root.addView(scroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        setContentView(root)
        val saved = getPreferences(MODE_PRIVATE).getString("lastResult", null)
        if (saved != null) output.text = saved
    }
    @Deprecated("Uses the platform document picker")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != 42 || resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        try {
            val content = contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText().take(2_000_000) } ?: ""
            val matches = platforms.mapNotNull { (name, domains) ->
                val found = domains.filter { domain -> Regex("(?i)(?:^|[^a-z0-9.-])" + Regex.escape(domain)).containsMatchIn(content) }
                if (found.isEmpty()) null else "• $name: reference found (${found.joinToString()})"
            }
            val report = if (matches.isEmpty()) "No known platform domains found in this file." else matches.joinToString("\n")
            output.text = "SCAN RESULTS\n\n$report\n\nA domain reference may be from a forwarded message, advertisement, or link. Verify the sender and original message before drawing conclusions."
            getPreferences(MODE_PRIVATE).edit().putString("lastResult", output.text.toString()).apply()
        } catch (e: Exception) { output.text = "Could not read file: ${e.message}" }
    }
}
