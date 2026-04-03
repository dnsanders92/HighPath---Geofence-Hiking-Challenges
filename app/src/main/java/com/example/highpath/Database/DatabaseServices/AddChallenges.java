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
                ulster.setRegion("Ulster");
                ulster.setShortDescription("Conquer the highest peaks across the nine historic counties of the northern province.");
                ulster.setLongDescription("Traverse the rugged landscapes of the north, from the granite dome of Slieve Donard to the quartzite spire of Errigal. This journey spans nine counties, taking you through the glens of Antrim, the wild Sperrins, and the hidden lakeland peaks of Fermanagh and Cavan. Claim your title by overlooking the wild Atlantic coastline and the rolling drumlins that define this historic province.");
                ulster.setCompletedSummits(0);
                ulster.setTotalSummits(9);
                ulster.setChallengeThumbnail(drawableToBytes(context, R.drawable.errigal));
                db.challengesDao().insertChallenge(ulster);

                // 2. CONNACHT CHALLENGE
                ChallengesEntity connacht = new ChallengesEntity();
                connacht.setChallengeName("High King of Connacht");
                connacht.setRegion("Connacht");
                connacht.setShortDescription("Ascend the five most prominent summits in the wild and rocky western province.");
                connacht.setLongDescription("Embrace the untamed West where mountains meet the ocean. Face the legendary Mweelrea and the holy slopes of Croagh Patrick, alongside the limestone plateaus of Sligo and Galway’s shimmering Twelve Bens. These mist-shrouded summits along the Wild Atlantic Way provide a dramatic backdrop for an epic adventure that tests both your physical endurance and your connection to the land.");
                connacht.setCompletedSummits(0);
                connacht.setTotalSummits(5);
                connacht.setChallengeThumbnail(drawableToBytes(context, R.drawable.crough_patrick));
                db.challengesDao().insertChallenge(connacht);

                // 3. LEINSTER CHALLENGE
                ChallengesEntity leinster = new ChallengesEntity();
                leinster.setChallengeName("High King of Leinster");
                leinster.setRegion("Leinster");
                leinster.setShortDescription("Complete a massive twelve-county circuit across the ancient eastern heartlands.");
                leinster.setLongDescription("This marathon of exploration covers twelve diverse counties, from the soaring heights of Lugnaquilla to the rolling Slieve Blooms. You will journey through rich plains and deep forests to find the highest points of the East. While some peaks are gentle, the logistical dedication required to stand as High King here honors the ancient paths of the High Kings of Tara.");
                leinster.setCompletedSummits(0);
                leinster.setTotalSummits(12);
                leinster.setChallengeThumbnail(drawableToBytes(context, R.drawable.lugnaquilla));
                db.challengesDao().insertChallenge(leinster);

                // 4. MUNSTER CHALLENGE
                ChallengesEntity munster = new ChallengesEntity();
                munster.setChallengeName("High King of Munster");
                munster.setRegion("Munster");
                munster.setShortDescription("Scale the formidable giants of the south, including Ireland's highest peak.");
                munster.setLongDescription("Widely considered the ultimate Irish hiking test, this challenge features the formidable MacGillycuddy's Reeks, home to Carrauntoohil. Your journey crosses the sandstone peaks of the Galtees, the Comeraghs, and the rugged hills of West Cork. Claiming this title proves your mastery over the giants of the south, following in the legendary footsteps of Brian Boru.");
                munster.setCompletedSummits(0);
                munster.setTotalSummits(6);
                munster.setChallengeThumbnail(drawableToBytes(context, R.drawable.carrauntoohil));
                db.challengesDao().insertChallenge(munster);

                Log.d(TAG, "All Provincial Challenges successfully inserted with concise descriptions!");

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