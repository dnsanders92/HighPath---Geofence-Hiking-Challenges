package com.example.highpath.Database.DatabaseServices;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.highpath.Database.Entities.ChallengesEntity;
import com.example.highpath.Database.LocalDatabase;
import com.example.highpath.R;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class AddChallenges {

    private static final String TAG = "AddChallenges";

    public void insertChallenges(final Context context) {
        new Thread(() -> {
            try {
                LocalDatabase db = LocalDatabase.getInstance(context);

                List<ChallengesEntity> existing = db.challengesDao().getAllChallenges();
                if (existing != null && !existing.isEmpty()) {
                    Log.d(TAG, "Challenges already exist. Skipping.");
                    return;
                }

                // 1. ULSTER CHALLENGE
                ChallengesEntity ulster = new ChallengesEntity();
                ulster.setChallengeName("High King of Ulster");
                ulster.setRegion("Ulster"); // Matches SummitsEntity province
                ulster.setShortDescription("Conquer the highest peaks across the nine historic counties of the northern province.");
                ulster.setLongDescription("The High King of Ulster challenge invites you to traverse the rugged landscapes of the north. From the massive granite dome of Slieve Donard in Down to the haunting quartzite spire of Errigal in Donegal, this journey covers nine unique counties. You will experience the ancient glens of Antrim, the wild Sperrin mountains of Tyrone, and the hidden lakeland peaks of Fermanagh and Cavan. Completing this circuit requires grit and determination, mirroring the resilience of the ancient O'Neill dynasty. Stand atop these summits to claim your title, overlooking the wild Atlantic coastline and the rolling drumlins that define this historic province.");
                ulster.setCompletedSummits(0);
                ulster.setTotalSummits(9);
                ulster.setChallengeThumbnail(drawableToBytes(context, R.drawable.errigal));
                db.challengesDao().insertChallenge(ulster);

                // 2. CONNACHT CHALLENGE
                ChallengesEntity connacht = new ChallengesEntity();
                connacht.setChallengeName("High King of Connacht");
                connacht.setRegion("Connacht"); // Matches SummitsEntity province
                connacht.setShortDescription("Ascend the five most prominent summits in the wild and rocky western province.");
                connacht.setLongDescription("Embrace the untamed spirit of the West with the High King of Connacht challenge. This path leads you through some of Ireland’s most dramatic terrain, where the mountains meet the ocean. You will face the legendary Mweelrea in Mayo, the highest in the province, and the holy slopes of Croagh Patrick. From the limestone plateaus of Sligo to the shimmering quartzite peaks of Galway’s Twelve Bens, each climb offers a deep connection to the land of Queen Medb. The mist-shrouded summits and sweeping views of the Wild Atlantic Way provide a backdrop for an epic adventure that tests both your physical endurance and your soul.");
                connacht.setCompletedSummits(0);
                connacht.setTotalSummits(5);
                connacht.setChallengeThumbnail(drawableToBytes(context, R.drawable.crough_patrick));
                db.challengesDao().insertChallenge(connacht);

                // 3. LEINSTER CHALLENGE
                ChallengesEntity leinster = new ChallengesEntity();
                leinster.setChallengeName("High King of Leinster");
                leinster.setRegion("Leinster"); // Matches SummitsEntity province
                leinster.setShortDescription("Complete a massive twelve-county circuit across the ancient eastern heartlands.");
                leinster.setLongDescription("The High King of Leinster is a marathon of exploration covering twelve diverse counties. This challenge takes you from the soaring granite heights of Lugnaquilla in Wicklow to the rolling Slieve Bloom Mountains in Laois and Offaly. You will journey through the rich agricultural plains of Meath and Kildare, finding the highest points hidden within deep forests and ancient royal sites. While some peaks are gentle, the sheer number of summits requires logistical dedication and a love for the varied emerald landscapes of the East. To stand as High King here is to have walked the very paths of the High Kings of Tara themselves.");
                leinster.setCompletedSummits(0);
                leinster.setTotalSummits(12);
                leinster.setChallengeThumbnail(drawableToBytes(context, R.drawable.lugnaquilla));
                db.challengesDao().insertChallenge(leinster);

                // 4. MUNSTER CHALLENGE
                ChallengesEntity munster = new ChallengesEntity();
                munster.setChallengeName("High King of Munster");
                munster.setRegion("Munster"); // Matches SummitsEntity province
                munster.setShortDescription("Scale the formidable giants of the south, including Ireland's highest peak.");
                munster.setLongDescription("The High King of Munster challenge is widely considered the ultimate test for any Irish hiker. It features the formidable MacGillycuddy's Reeks in Kerry, home to Carrauntoohil, the highest point on the island. Your journey will take you across the sandstone peaks of the Galtee Mountains in Tipperary and Limerick, the Comeraghs in Waterford, and the rugged hills of West Cork and Clare. These mountains are steep, weather-beaten, and immensely rewarding, offering panoramic views of the southern valleys and the Atlantic horizon. Claiming this title proves your mastery over the giants of the south, following in the legendary footsteps of Brian Boru.");
                munster.setCompletedSummits(0);
                munster.setTotalSummits(6);
                munster.setChallengeThumbnail(drawableToBytes(context, R.drawable.carrauntoohil));
                db.challengesDao().insertChallenge(munster);

                Log.d(TAG, "All Provincial Challenges successfully inserted with region mapping!");

            } catch (Exception e) {
                Log.e(TAG, "Error inserting provincial challenges", e);
            }
        }).start();
    }

    private static byte[] drawableToBytes(Context context, int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        if (bitmap == null) return new byte[0];

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }
}