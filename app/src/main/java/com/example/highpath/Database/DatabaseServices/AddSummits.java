package com.example.highpath.Database.DatabaseServices;

import android.content.Context;
import android.util.Log;
import com.example.highpath.Database.Entities.SummitsEntity;
import com.example.highpath.Database.LocalDatabase;
import java.util.ArrayList;
import java.util.List;

public class AddSummits {

    private static final String TAG = "AddSummits";

    public void insertSummits(final Context context) {
        new Thread(() -> {
            LocalDatabase db = LocalDatabase.getInstance(context);

            if (db.summitsDao().getAllSummits().size() > 0) {
                Log.d(TAG, "Summits already exist. Skipping.");
                return;
            }

            List<SummitsEntity> summits = new ArrayList<>();

            // --- LEINSTER ---
            summits.add(create("Lugnaquilla", "Leinster", 925, 52.966444, -6.465139, 849,
                    "The highest peak in the Wicklow Mountains and Leinster, Lugnaquilla is a massive granite massif known as the 'Hollow of the Grouse.' Its plateau is flanked by the dramatic North and South Prisons, glacial coums steeped in local lore of ancient captives. On a rare clear day, the vista extends across the Irish Sea to the peaks of Snowdonia in Wales, making it a crowning jewel of the eastern coastline."));

            summits.add(create("Mount Leinster", "Leinster", 794, 52.617111, -6.783056, 505,
                    "Rising above the Blackstairs range, Mount Leinster is a site of ancient spiritual significance. Topped by a Bronze Age burial cairn called 'The Nine Stones,' mythology suggests it was a gathering place for the Tuatha Dé Danann. While it now hosts a major transmission mast, the mountain remains a rugged, wind-swept wilderness that offers hikers an incredible 360-degree view of the fertile valleys of Carlow and Wexford."));

            summits.add(create("Kippure", "Leinster", 757, 53.175278, -6.331389, 250,
                    "Marking the border of Dublin and Wicklow, Kippure is a vast peat-covered dome and the source of the River Liffey. Its name, meaning 'Trunk of the Yew Tree,' hints at a time when sacred forests covered these now-boggy slopes. The mountain has served as a landmark for millennia, preserving ancient artifacts in its deep blankets of moss, and remains the highest point for those looking out over the sprawl of Dublin city."));

            summits.add(create("Arderin", "Leinster", 527, 53.003611, -7.652500, 250,
                    "Arderin is the highest point of the Slieve Bloom Mountains, one of the oldest mountain ranges in Europe. The name translates to 'Height of Ireland,' and according to legend, the great warrior Fionn mac Cumhaill was raised in the nearby forests. Today, Arderin offers a serene, isolated experience, looking out over the central limestone plains of Ireland and the lush, green heartlands of Laois and Offaly."));

            summits.add(create("Brandon Hill", "Leinster", 515, 52.510278, -6.911667, 448,
                    "The highest mountain in County Kilkenny, Brandon Hill stands as a solitary guardian over the River Barrow. Its slopes are covered in ancient woodland and heather, offering a haven for local wildlife. The summit features a large cross and provides views that stretch from the South Midlands to the Atlantic coast. It is a place of quiet reflection, often associated with the early Christian monastic settlements that dotted the surrounding valleys."));

            summits.add(create("Cupidstown Hill", "Leinster", 379, 53.221667, -6.524444, 100,
                    "Located on the edge of the Wicklow Mountains, Cupidstown Hill is the highest point in County Kildare. Though modest in height, it offers a commanding view of the Curragh and the vast plains of the Thoroughbred County. Historically, the area was a strategic lookout point for ancient tribes. Today, it serves as a peaceful escape for locals, bridging the gap between the rugged highlands and the rolling green fields of the east."));

            summits.add(create("Slieve Foye", "Leinster", 589, 54.049444, -6.216389, 450,
                    "The highest of the Cooley Mountains, Slieve Foye is the legendary setting of the 'Táin Bó Cúailnge' (The Cattle Raid of Cooley). It is said that the warrior Cú Chulainn rests within these jagged limestone ridges. The climb is steep and technical, rewarding hikers with breathtaking views over Carlingford Lough and the Mourne Mountains across the water, blending epic Irish mythology with raw coastal beauty."));

            summits.add(create("Slieve na Calliagh", "Leinster", 276, 53.743333, -7.118611, 150,
                    "Known as the 'Hill of the Witch,' this is the highest point in County Meath. It is home to the Loughcrew Megalithic tombs, which date back over 5,000 years. During the equinoxes, sunlight illuminates the backstone of the main chamber, a testament to the astronomical skill of the Neolithic people. To stand here is to walk among the oldest sacred sites in Europe, overlooking the ancient Kingdom of Meath."));

            summits.add(create("Mullaghmeen", "Leinster", 258, 53.766389, -7.283889, 100,
                    "Mullaghmeen is the highest point in County Westmeath and is located within the largest planted beech forest in Ireland. The hill provides a unique woodland hiking experience, with trails winding through towering trees and diverse flora. Historically, the forest provided timber for local construction and fuel for centuries. From the summit cairn, hikers can see across the lakelands of Westmeath and into the neighboring northern counties."));

            summits.add(create("Corn Hill", "Leinster", 278, 53.791111, -7.712222, 100,
                    "Also known as Cairn Hill, this is the highest point in County Longford. It is crowned by a passage tomb that local folklore claims is the burial place of an ancient queen. Because of its prominence in the flat midlands, it has long served as a vital communication point, now hosting a major television mast. The climb offers a rare perspective over the central Shannon basin and the patchwork fields of the Irish heartland."));

            // --- MUNSTER ---
            summits.add(create("Carrauntoohil", "Munster", 1038, 51.999444, -9.742778, 1038,
                    "The roof of Ireland, Carrauntoohil is the heart of the MacGillycuddy's Reeks. Carved by ancient glaciers into sharp arêtes and deep valleys, its name translates to 'reversed sickle.' It is a place of challenge, topped by a towering steel cross. Standing here means standing at the highest point of the island, surrounded by a landscape of jagged rock and Atlantic mist that feels truly primordial and ancient."));

            summits.add(create("Galtymore", "Munster", 918, 52.365278, -8.178333, 898,
                    "Galtymore is the king of the Galty range, a massive sandstone ridge straddling Tipperary and Limerick. Known as the 'Mountains of the Gods,' the peak is associated with ancient deities of the mist. Its north face drops into spectacular glacial cirques containing hidden mountain lakes. From the summit, the 'Golden Vale' spreads out below like a green tapestry, showcasing the heart of Munster’s rich agricultural heritage."));

            summits.add(create("Knockboy", "Munster", 706, 51.801667, -9.444444, 500,
                    "Knockboy, or 'An Cnoc Buí' (The Yellow Hill), is the highest peak in County Cork, located in the Shehy Mountains. It sits on the border with Kerry and offers wild, remote terrain characterized by sandstone ridges and blanket bogs. The area is steeped in the history of West Cork's resistance and survival. On clear days, the view reaches out over Bantry Bay and the rugged, indented coastline of the southwest Atlantic."));

            summits.add(create("Knockmealdown", "Munster", 794, 52.228056, -7.919444, 680,
                    "The highest point of the Knockmealdown Mountains on the Waterford and Tipperary border. This range is famous for its vibrant purple heather and the scenic Vee Pass. Folklore tells of a local man named Sam Anderson who is buried standing upright on the summit to keep watch over his lands. The mountain provides a dramatic backdrop to the historic town of Lismore and the fertile Blackwater Valley below."));

            summits.add(create("Moylussa", "Munster", 532, 52.846111, -8.513889, 300,
                    "Moylussa is the highest point in County Clare, overlooking the majestic Lough Derg and the River Shannon. The trails lead through managed forestry into wild moorland. Historically, this area was a stronghold for the O'Brien clan, the Kings of Munster. The summit offers a peaceful vantage point to watch the boats navigate the Shannon, bridging the gap between the wild Burren to the west and the lush valley to the east."));

            // --- ULSTER ---
            summits.add(create("Slieve Donard", "Ulster", 850, 54.231389, -5.922222, 822,
                    "Dominating the Mourne Mountains, Slieve Donard is named after Saint Domhanghart, who lived as a hermit on its slopes in the 5th century. The mountain is crossed by the Mourne Wall, a Herculean dry-stone structure built to enclose a reservoir catchment. Legend tells of a hidden cave on the summit leading to the spirit world, while the physical view reaches across the sea to Scotland and the Isle of Man."));

            summits.add(create("Errigal", "Ulster", 751, 55.034167, -8.112222, 680,
                    "Errigal is Donegal's most recognizable landmark, a gleaming cone of white quartzite. Often called the 'Shining Mountain,' it is the highest of the Seven Sisters. The narrow 'One Man's Path' at the summit is a test of nerves, offering a vertiginous perspective over the wild Atlantic and the haunting ruins of the Poisoned Glen below, where folklore says ancient giants once fought for control of the land."));

            summits.add(create("Sawel Mountain", "Ulster", 678, 54.819444, -7.037500, 650,
                    "Sawel Mountain is the highest point in the Sperrins, a range that spans Derry and Tyrone. The mountain is characterized by its rounded, peat-covered slopes and ancient glacial history. Gold has been found in the streams flowing from its base, leading to many local legends of hidden treasure. From the top, the entire northern coastline is visible, providing a sense of the vast, wild beauty of the Ulster interior."));

            summits.add(create("Cuilcagh", "Ulster", 665, 54.200556, -7.800556, 600,
                    "Cuilcagh straddles the border of Fermanagh and Cavan and is famous for its 'Stairway to Heaven' boardwalk that protects the rare blanket bog. The summit plateau is a wild karst landscape, part of a UNESCO Global Geopark. Historically, the mountain was a source of limestone and coal. Its name means 'Chalky Peak,' and it offers a dramatic view over the lakelands of Fermanagh and the Shannon Pot, where Ireland's longest river begins."));

            summits.add(create("Slieve Gullion", "Ulster", 573, 54.127222, -6.431389, 400,
                    "The highest point in County Armagh, Slieve Gullion is an extinct volcano and the center of the Ring of Gullion. It is rich in mythology, particularly the tale of Fionn mac Cumhaill being bewitched by the Cailleach (the Witch) in the lake at the summit. The mountain features the highest passage tomb in Ireland. It is a place where history and legend are physically carved into the volcanic rock of the landscape."));

            summits.add(create("Trostan", "Ulster", 550, 55.042500, -6.155833, 350,
                    "Trostan is the highest point in County Antrim and the Antrim Plateau. It is a wild, peat-covered upland that overlooks the famous Glens of Antrim. The mountain is part of a landscape formed by massive volcanic eruptions millions of years ago. It offers a remote hiking experience with views that stretch across the North Channel to the Mull of Kintyre in Scotland, reflecting the ancient connections between the two lands."));

            summits.add(create("Sliabh Beagh", "Ulster", 380, 54.316111, -7.116389, 200,
                    "Sliabh Beagh is a vast upland area on the border of Monaghan, Fermanagh, and Tyrone. Its name means 'Mountain of Birches.' The area is a protected blanket bog, home to rare birds like the Hen Harrier. Mythology suggests it is the burial place of Bith, one of the first people to arrive in Ireland before the Great Flood. It is a quiet, contemplative landscape that feels untouched by the modern world."));

            // --- CONNACHT ---
            summits.add(create("Mweelrea", "Connacht", 814, 53.637222, -9.835278, 750,
                    "Mweelrea, the 'Grey Bald Hill,' stands as the highest point in Connacht, overlooking the deep waters of Killary Fjord. It is a massive mountain of steep cliffs and hidden plateaus. Its presence has long been a guide for sailors on the Atlantic. Steeped in the history of the rugged western coast, the mountain offers a sense of total isolation, where the only sounds are the wind and the crashing waves below."));

            summits.add(create("Benbaun", "Connacht", 729, 53.522500, -9.833611, 600,
                    "Benbaun is the highest of the Twelve Bens in Connemara, Galway. Its white quartzite summit stands out against the dark, boggy valleys below. The mountain is central to the wild heart of Connemara, a landscape that has inspired artists and writers for centuries. The climb is rugged and rock-strewn, offering a true mountain experience with views of the Atlantic, the Maumturks, and the vast, shimmering lakes of Galway."));

            summits.add(create("Truskmore", "Connacht", 647, 54.372500, -8.371389, 600,
                    "Truskmore is the plateau-topped giant of the Dartry Mountains in Sligo and Leitrim. Associated with the poetry of W.B. Yeats and the legend of Diarmuid and Gráinne, its limestone cliffs are rich with ancient fossils. The summit provides a hauntingly beautiful view over Sligo Bay, where the lines between the physical landscape and Irish literary history become blurred in the Atlantic mist that often rolls over its heights."));

            summits.add(create("Mount Mary", "Connacht", 163, 53.518611, -8.384722, 50,
                    "Mount Mary is the highest point in County Roscommon. While modest in height compared to the coastal giants, it provides an important vantage point over the rolling central plains. Historically, the area was a center for agriculture and local trade. The gentle slopes are home to ancient field systems and offer a peaceful walk with views that capture the quiet beauty and rural heritage of the Irish midlands and the western province."));

            for (SummitsEntity s : summits) {
                db.summitsDao().insertSummit(s);
            }
            Log.d(TAG, "Successfully inserted 26 high points with high-precision coordinates and historical descriptions.");
        }).start();
    }

    private SummitsEntity create(String name, String province, int elevation, double lat, double lon, int prominence, String description) {
        SummitsEntity s = new SummitsEntity();
        s.setSummitName(name);
        s.setRegion(province);
        s.setElevation(elevation);
        s.setLatitude(lat);
        s.setLongitude(lon);
        s.setProminence(prominence);
        s.setDescription(description);
        s.setCompleted(false);
        return s;
    }
}