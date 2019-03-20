package com.givealike.givealike2;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SQLiteDatabase myDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setTitle("");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myDatabase = this.openOrCreateDatabase("Hashtags",MODE_PRIVATE,null);
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS hashtags (category VARCHAR, subcategory VARCHAR , name VARCHAR)");
        Cursor c = myDatabase.rawQuery("SELECT * FROM hashtags",null);
        if(c.getCount()>0){
            Log.i("DATABASE",Integer.toString(c.getCount()));
        }else{
            Log.i("DATABASE","Initialize database..");
            initiateDatabase();
        }
        c.close();


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView name = navigationView.getHeaderView(0).findViewById(R.id.nav_display_name);
        TextView email = navigationView.getHeaderView(0).findViewById(R.id.nav_email);
        ImageView profile_pic = navigationView.getHeaderView(0).findViewById(R.id.nav_profile_url);

        name.setText(getIntent().getStringExtra("name"));
        email.setText(getIntent().getStringExtra("email"));

        try {
            if(!getIntent().getStringExtra("profile_pic").equals(""))
                Picasso.get().load(getIntent().getStringExtra("profile_pic")).into(profile_pic);
        }catch (Exception e){
            e.printStackTrace();
        }

        setFragment(new MainFragment());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.instagram:
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                if (launchIntent != null) {
                    startActivity(launchIntent);//null pointer check in case package name was not found
                }else{
                    Toast.makeText(getApplicationContext(),"Instagram is not installed.",Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            setFragment(new MainFragment());
        }else if ( id == R.id.nav_log_out){
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestProfile()
                    .build();

            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            mGoogleSignInClient.signOut();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);

        }else if(id == R.id.nav_privacy){
            setFragment(new PrivacyFragment());
        }else if(id == R.id.nav_about){
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setView(R.layout.about_dialog)
                    .setPositiveButton("OK",null)
                    .setNeutralButton("Contact Us", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

            TextView text = alertDialog.findViewById(R.id.version);
            text.setText(getString(R.string.v)+BuildConfig.VERSION_NAME);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.nav_enter,R.anim.nav_exit);
        transaction.replace(R.id.mainConstraint,fragment);
        transaction.commit();
    }

    public void initiateDatabase(){
        myDatabase.execSQL("INSERT INTO hashtags (category,subcategory,name) VALUES " +
                "('Animals','Animals General','#animals #animal #pet #dog #cat #dogs #cats #photooftheday #cute #instagood #animales #cute #love #nature #animallovers #pets_of_instagram #petstagram #petsagram')," +
                "('Animals','Dogs','#dog #dog #puppy #pup #cute #eyes #instagood #dogs_of_instagram #pet #pets #animal #animals #petstagram #petsagram #dogsitting #photooftheday #dogsofinstagram #ilovemydog #instagramdogs #nature #dogstagram #dogoftheday #lovedogs #lovepuppies #hound #adorable #doglover #instapuppy #instadog')," +
                "('Animals','Cats','#cat #cats #catsagram #catstagram #instagood #pussy #pussycat #kitten #kitty #kittens #pets #animal #animals #petstagram #petsagram #photooftheday #catsofinstagram #ilovemycat #instagramcats #nature #catoftheday #lovecats #furry #sleeping #lovekittens #adorable #catlover #instacat')," +
                "('Animals','Horses','#horses #horse #horsesofinstagram #horseshow #horseshoe #horses_of_instagram #horsestagram #instahorses #wild #mane #instagood #grass #field #farm #nature #pony #ponies #ilovemyhorse #babyhorse #beautiful #pretty #photooftheday #gallop #jockey #rider #riders #riding')," +
                "('Animals','Insects','#insects #insect #bug #bugs #bugslife #macro #closeup  #animals #animals #instanature #instagood #macrogardener #macrophotography #creature #creatures #macro_creature_feature #photooftheday #wildlife #nature_shooters #earth #naturelover #lovenature')," +
                "('Animals','Fish','#fish #aquarium #fishtank #fishporn #instafish #instagood #swim #swimming #water #coral #reef #reeftank #tropical #tropicalfish #aquaria #photooftheday #saltwater #freshwater #beautiful #ocean #watertank')," +
                "('Art/Photography','Art','#art #illustration #drawing #draw #picture #photography #sketch #sketchbook #paper #pen #pencil #artsy #instaart #beautiful #instagood #gallery #masterpiece #creative #photooftheday #instaartist #graphic #graphics #artoftheday')," +
                "('Art/Photography','Photography','#photography #photo #photos #pic #pics #picture #pictures #snapshot #art #beautiful #instagood #picoftheday #photooftheday #color #all_shots #exposure #composition #focus #capture #moment')," +
                "('Art/Photography','HDR','#hdr #hdriphoneographer #hdrspotters #hdrstyles_gf #hdri #hdroftheday #hdriphonegraphy #hdrepublic #hdr_lovers #awesome_hdr #instagood #hdrphotography #photooftheday #hdrimage #hdr_gallery #hdr_love #hdrfreak #hdrama #hdrart #hdrphoto #hdrfusion #hdrmania #hdrstyles #ihdr #str8hdr #hdr_edits')," +
                "('Art/Photography','Black & White','#blackandwhite #bnw #monochrome #instablackandwhite #monoart #insta_bw #bnw_society #bw_lover #bw_photooftheday #photooftheday #bw #instagood #bw_society #bw_crew #bwwednesday #insta_pick_bw #bwstyles_gf #irox_bw #igersbnw #bwstyleoftheday #monotone #monomood #monochromatic#noir #fineart_photobw')," +
                "('Electronics/Phone','Electronics','#electronics #technology #tech #electronic #device #gadget #gadgets #instatech #instagood #geek #techie #nerd #techy #photooftheday #computers #laptops #hack #screen')," +
                "('Electronics/Phone','iPhone','#iphone #iphoneonly #apple #appleiphone #ios #iphone3g #iphone3gs #iphone4 #iphone5 #technology #electronics #mobile #instagood #instaiphone #phone #photooftheday #smartphone #iphoneography #iphonegraphy #iphoneographer #iphoneology #iphoneographers #iphonegraphic #iphoneogram #teamiphone')," +
                "('Electronics/Phone','Android','#android #androidonly #google #googleandroid #droid #instandroid #instaandroid #instadroid #instagood #ics #jellybean #samsung #samsunggalaxys2 #samsunggalaxy #phone #smartphone #mobile #androidography #androidographer #androidinstagram #androidnesia #androidcommunity #teamdroid #teamandroid')," +
                "('Entertainment','Music','#music #genre #song #songs #melody #hiphop #rnb #pop #rap #dubstep #instagood #beat #beats #jam #myjam #party #partymusic #newsong #lovethissong #remix #favoritesong #bestsong #photooftheday #bumpin #repeat #listentothis #goodmusic #instamusic')," +
                "('Entertainment','Movies','#movies #theatre #video #movie #film #films #videos #actor #actress #cinema #dvd #amc #instamovies #star #moviestar #photooftheday #hollywood #goodmovie #instagood #flick #flicks #instaflick #instaflicks')," +
                "('Entertainment','Books','#books #book #read #reading #reader #page #pages #paper #instagood #kindle #nook #library #author #bestoftheday #bookworm #readinglist #love #photooftheday #imagine #plot #climax #story #literature #literate #stories #words #text')," +
                "('Entertainment','Video Games','#videogames #games #gamer #gaming #instagaming #instagamer #playinggames #online #photooftheday #onlinegaming #videogameaddict #instagame #instagood #gamestagram #gamerguy #gamergirl #gamin #video #game #igaddict #winning #play #playing')," +
                "('Family/Kids','Family General','#family #fam #mom #dad #brother #sister #brothers #sisters #bro #sis #siblings #love #instagood #father #mother #related #fun #photooftheday #children #kids #life #happy #familytime #cute #smile #fun')," +
                "('Family/Kids','Babies','#baby #babies #adorable #cute #cuddly #cuddle #small #lovely #love #instagood #kid #kids #beautiful #life #sleep #sleeping #children #happy #igbabies #childrenphoto #toddler #instababy #infant #young #photooftheday #sweet #tiny #little #family')," +
                "('Family/Kids','Kids','#kids #kid #instakids #child #children #childrenphoto #love #cute #adorable #instagood #young #sweet #pretty #handsome #little #photooftheday #fun #family #baby #instababy #play #happy #smile #instacute')," +
                "('Fashion','Fashion General','#fashion #style #stylish #love #me #cute #photooftheday #nails #hair #beauty #beautiful #instagood #pretty #swag #pink #girl #girls #eyes #design #model #dress #shoes #heels #styles #outfit #purse #jewlery #shopping #glam')," +
                "('Fashion','Fashion (Girls)','#fashion #style #stylish #love #me #cute #photooftheday #nails #hair #beauty #beautiful #instagood #instafashion #pretty #girly #pink #girl #girls #eyes #model #dress #skirt #shoes #heels #styles #outfit #purse #jewlery #shopping')," +
                "('Fashion','Fashion (Guys)','#fashion #swag #style #stylish #me #swagger #cute #photooftheday #jacket #hair #pants #shirt #instagood #handsome #cool #polo #swagg #guy #boy #boys #man #model #tshirt #shoes #sneakers #styles #jeans #fresh #dope')," +
                "('Fashion','Nails','#nails #nail #fashion #style #cute #beauty #beautiful #instagood #pretty #girl #girls #stylish #sparkles #styles #gliter #nailart #art #opi #photooftheday #essie #unhas #preto #branco #rosa #love #shiny #polish #nailpolish #nailswag')," +
                "('Fashion','Hair','#hair #hairstyle #instahair #hairstyles #haircolour #haircolor #hairdye #hairdo #haircut #longhairdontcare #braid #fashion #instafashion #straighthair #longhair #style #straight #curly #black #brown #blonde #brunette #hairoftheday #hairideas #braidideas #perfectcurls #hairfashion #hairofinstagram #coolhair')," +
                "('Fashion','Makeup','#makeup #instamakeup #cosmetic #cosmetics #TFLers #fashion #eyeshadow #lipstick #gloss #mascara #palettes #eyeliner #lip #lips #tar #concealer #foundation #powder #eyes #eyebrows #lashes #lash #glue #glitter #crease #primers #base #beauty #beautiful')," +
                "('Fashion','Sneakers','#shoes #shoe #kicks #instashoes #instakicks #sneakers #sneaker #sneakerhead #sneakerheads #solecollector #soleonfire #nicekicks #igsneakercommunity #sneakerfreak #sneakerporn #shoeporn #fashion #swag #instagood #fresh #photooftheday #nike #sneakerholics #sneakerfiend #shoegasm #kickstagram #walklikeus #peepmysneaks #flykicks')," +
                "('Fashion','Tattoos','#tattoo #tattoos #tat #ink #inked #TFLers #tattooed #tattoist #coverup #art #design #instaart #instagood #sleevetattoo #handtattoo #chesttattoo #photooftheday #tatted #instatattoo #bodyart #tatts #tats #amazingink #tattedup #inkedup')," +
                "('Fashion','Piercings','#piercing #piercings #pierced #TFLers #bellyrings #navel #earlobe #ear #photooftheday #bellybuttonring #lipring #instagood #modifications #bodymods #piercingaddict #bellybar #bellybuttonpiercing')," +
                "('Follow/Like/Comment','FSLC (Follow/Shoutout/Like/Comment)','#fslc #followshoutoutlikecomment #TagsForLikesFSLC #follow #shoutout #like #comment #f4f #s4s #l4l #c4c #followback #shoutoutback #likeback #commentback #love #instagood #photooftheday #pleasefollow #pleaseshoutout #pleaselike #pleasecomment #teamfslcback #fslcback #follows #shoutouts #likes #comments #fslcalways')," +
                "('Follow/Like/Comment','Follow','#follow #f4f #followme #TFLers #followforfollow #follow4follow #teamfollowback #followher #followbackteam #followhim #followall #followalways #followback #me #love #pleasefollow #follows #follower #following')," +
                "('Follow/Like/Comment','Shoutout','#shoutout #shoutouts #shout #out #TFLers #shoutouter #instagood #s4s #shoutoutforshoutout #shoutout4shoutout #so #so4so #photooftheday #ilovemyfollowers #love #sobackteam #soback #follow #f4f #followforfollow #followback #followhim #followher #followall #followme #shout_out')," +
                "('Follow/Like/Comment','Like','#like #like4like #TFLers #liker #likes #l4l #likes4likes #photooftheday #love #likeforlike #likesforlikes #liketeam #likeback #likebackteam #instagood #likeall #likealways #liking')," +
                "('Follow/Like/Comment','Comment','#comment #comment4comment #TFLers #c4c #commenter #comments #commenting #love #comments4comments #instagood #commentteam #commentback #commentbackteam #commentbelow #photooftheday #commentall #commentalways #pleasecomment')," +
                "('Food','Food General','#food #foodporn #yum #instafood #yummy #amazing #instagood #photooftheday #sweet #dinner #lunch #breakfast #fresh #tasty #foodie #delish #delicious #eating #foodpic #foodpics #eat #hungry #foodgasm #hot #foods')," +
                "('Food','Dessert','#dessert #food #desserts #yum #yummy #amazing #instagood #instafood #sweet #chocolate #cake #icecream #dessertporn #delish #foods #delicious #tasty #eat #eating #hungry #foodpics #sweettooth')," +
                "('Food','Drinks','#drink #drinks #slurp #pub #bar #liquor #yum #yummy #thirst #thirsty #instagood #cocktail #cocktails #drinkup #glass #can #photooftheday #beer #beers #wine')," +
                "('Holidays/Party','Halloween','#halloween #oct #october #30 #scary #spooky #boo #scared #costume #ghost #pumpkin #pumpkins #pumpkinpatch #carving #candy #orange #jackolantern #creepy #fall #trickortreat #trick #treat #instagood #party #holiday #celebrate #bestoftheday #hauntedhouse #haunted')," +
                "('Holidays/Party','Party','#party #partying #fun #instaparty #instafun #instagood #bestoftheday #crazy #friend #friends #besties #guys #girls #chill #chilling #kickit #kickinit #cool #love #memories #night #smile #music #outfit #funtime #funtimes #goodtime #goodtimes #happy')," +
                "('Holidays/Party','Birthday','#birthday #bday #party #instabday #bestoftheday #birthdaycake #cake #friends #celebrate #photooftheday #instagood #candle #candles #happy #young #old #years #instacake #happybirthday #instabirthday #born #family')," +
                "('Nature','Nature General','#nature  #sky #sun #summer #beach #beautiful #pretty #sunset #sunrise #blue #flowers #night #tree #twilight #clouds #beauty #light #cloudporn #photooftheday #love #green #skylovers #dusk #weather #day #skypainters #red #iphonesia #mothernature')," +
                "('Nature','Beach','#beach #sun #nature #water #TFLers #ocean #lake #instagood #photooftheday #beautiful #sky #clouds #cloudporn #fun #pretty #sand #reflection #amazing #beauty #beautiful #shore #waterfoam #seashore #waves #wave')," +
                "('Nature','Sunset/Sunrise','#sunset #sunrise #sun #TFLers #pretty #beautiful #red #orange #pink #sky #skyporn #cloudporn #nature #clouds #horizon #photooftheday #instagood #gorgeous #warm #view #night #morning #silhouette #instasky #all_sunsets')," +
                "('Nature','Flowers','#flowers #flower #petal #petals #nature #beautiful #love #pretty #plants #blossom #sopretty #spring #summer #flowerstagram #flowersofinstagram #flowerstyles_gf #flowerslovers #flowerporn #botanical #floral #florals #insta_pick_blossom #flowermagic #instablooms #bloom #blooms #botanical #floweroftheday')," +
                "('Nature','Sun','#sun #sunny #sunnyday #sunnydays #sunlight #light #sunshine #shine #nature #sky #skywatcher #thesun #sunrays #photooftheday #beautiful #beautifulday #weather #summer #goodday #goodweater #instasunny #instasun #instagood #clearskies #clearsky #blueskies #lookup #bright #brightsun')," +
                "('Nature','Clouds','#clouds #cloud #cloudporn #weather #lookup #sky #skies #skyporn #cloudy #instacloud #instaclouds #instagood #nature #beautiful #gloomy #skyline #horizon #instasky #epicsky #crazyclouds #photooftheday #cloud_skye #skyback #insta_sky_lovers #iskyhub #skypainters')," +
                "('Other','School','#school #class #classess #teacher #teachers #student #students #instagood #classmates #classmate #peer #work #homework #bored #books #book #photooftheday #textbook #textbooks #messingaround')," +
                "('Other','Work','#work #working #job #myjob #office #company #bored #grind #mygrind #dayjob #ilovemyjob #dailygrind #photooftheday #business #biz #life #workinglate #computer #instajob #instalife #instagood #instadaily')," +
                "('Other','Spiritual','#spiritual #faith #faithful #god #grace #pray #prayers #praying #amen #believe #religion #coexist #spirituality #trust #peace #calm #mind #soul #hope #destiny #wisdom #compassion #forgiveness #thankful #knowledge #meditation #life #meditate #guidance')," +
                "('Other','Money','#money #cash #green #dough #bills #crisp #benjamin #benjamins #franklin #franklins #bank #payday #hundreds #twentys #fives #ones #100s #20s #greens #photooftheday #instarich #instagood #capital #stacks #stack #bread #paid')," +
                "('Other','Colors','#colors #color #colorful #red #orange #yellow #green #blue #indigo #violet #beautiful #rainbow #rainbowcolors #colour #roygbiv #instacolor #instagood #colorgram #colores #vibrant #multicolor #multicolored #instacolorful #colorworld')," +
                "('Other','Funny','#funny #lol #lmao #lmfao #hilarious #laugh #laughing #tweegram #fun #friends #photooftheday #friend #wacky #crazy #silly #witty #instahappy #joke #jokes #joking #epic #instagood #instafun #funnypictures #haha #humor')," +
                "('Other','Quotes','#quote #quotes #comment #comments #TFLers #tweegram #quoteoftheday #song #funny #life #instagood #love #photooftheday #igers #instagramhub #tbt #instadaily #true #instamood #nofilter #word')," +
                "('Other','Throwback Thursday','#tbt #throwbackthursday #throwbackthursdays #tbts #throwback #tb #instatbt #instatb #reminisce #reminiscing #backintheday #photooftheday #back #memories #instamemory #miss #old #instamoment #instagood #throwbackthursdayy #throwbackthursdayyy')," +
                "('Other','Kik','#kik #kikme #kikmessenger #TFLers #kikmenow #kikit #kikster #kikmegirls #kikmeguys #kikmessanger #kikmeimbored #kikmeplease #kikmessage #kikmee #kikmemaybe #pleasekik #letskik #instakik')," +
                "('Popular','Most Popular','#love  #TFLers #instagood #tweegram #photooftheday #me #instamood #cute #iphonesia #summer #tbt #igers #picoftheday #girl #instadaily #instagramhub #beautiful #iphoneonly #bestoftheday #food #jj #webstagram #picstitch #sky #follow #nofilter #happy #fashion #sun')," +
                "('Popular','2nd Popular','#fun #instagramers  #food #smile #followme #pretty #beach #nature #onedirection #friends #dog #hair #sunset #throwbackthursday #instagood #swag #blue #statigram #hot #funny #life #art #instahub #photo #lol #cool #pink #bestoftheday #clouds')," +
                "('Popular','3rd Popular','#amazing  #like #all_shots #textgram #family #instago #igaddict #awesome #girls #instagood #my #bored #baby #music #red #green #water #harrystyles #bestoftheday #black #party #white #yum #flower #2012 #night #instalove #niallhoran #jj_forum')," +
                "('Social/Love/People','Social/People General','#love #photooftheday #me #instamood #cute #igers #picoftheday #girl #guy #beautiful #fashion #instagramers #follow #smile #pretty #followme #friends #hair #swag #photo #life #funny #cool #hot #bored #portrait #baby #girls #iphonesia')," +
                "('Social/Love/People','Girls','#girl #girls #love #TFLers #me #cute #picoftheday #beautiful #photooftheday #instagood #fun #smile #pretty #follow #followme #hair #friends #swag #sexy #hot #cool #kik #fashion #igers #instagramers #style #sweet #eyes #beauty')," +
                "('Social/Love/People','Guys','#guys #guy #boy #TFLers #boys #love #me #cute #handsome #picoftheday #photooftheday #instagood #fun #smile #dude #follow #followme #swag #sexy #hot #cool #kik #igers #instagramers #eyes')," +
                "('Social/Love/People','Love','#love #couple #cute #adorable #kiss #kisses #hugs #romance #forever #girlfriend #boyfriend #gf #bf #bff #together #photooftheday #happy #me #girl #boy #beautiful #instagood #instalove #loveher #lovehim #pretty #fun #smile #xoxo')," +
                "('Social/Love/People','Friends','#friend #friends #fun #funny #love #instagood #igers #friendship #party #chill #happy #cute #photooftheday #live #forever #smile #bff #bf #gf #best #bestfriend #lovethem #bestfriends #goodfriends #besties #awesome #memories #goodtimes #goodtime')," +
                "('Social/Love/People','Good Morning','#goodmorning #morning #day #daytime #sunrise #morn #awake #wakeup #wake #wakingup #ready #sleepy #breakfast #tired #sluggish #bed #snooze #instagood #earlybird #sky #photooftheday #gettingready #goingout #sunshine #instamorning #work #early #fresh #refreshed')," +
                "('Social/Love/People','Good Night','#goodnight #night #nighttime #sleep #sleeptime #sleepy #sleepyhead #tired #goodday #instagood #instagoodnight #photooftheday #nightynight #lightsout #bed #bedtime #rest #nightowl #dark #moonlight #moon #out #passout #knockout #knockedout')," +
                "('Sport/Active/Travel','Travel','#travel #traveling #TFLers #vacation #visiting #instatravel #instago #instagood #trip #holiday #photooftheday #fun #travelling #tourism #tourist #instapassport #instatraveling #mytravelgram #travelgram #travelingram #igtravel')," +
                "('Sport/Active/Travel','Cars','#cars #car #ride #drive #driver #sportscar #vehicle #vehicles #street #road #freeway #highway #sportscars #exotic #exoticcar #exoticcars #speed #tire #tires #spoiler #muffler #race #racing #wheel #wheels #rim #rims #engine #horsepower')," +
                "('Sport/Active/Travel','Motorcycles','#motorcycle #motorcycles #bike #ride #rideout #bike #biker #bikergang #helmet #cycle #bikelife #streetbike #cc #instabike #instagood #instamotor #motorbike #photooftheday #instamotorcycle #instamoto #instamotogallery #supermoto #cruisin #cruising #bikestagram')," +
                "('Sport/Active/Travel','Skateboarding','#skateboarding #skating #skater #instaskater #sk8 #sk8er #sk8ing #sk8ordie #photooftheday #board #longboard #longboarding #riding #kickflip #ollie #instagood #wheels #skatephotoaday #skateanddestroy #skateeverydamnday #skatespot #skaterguy #skatergirl #skatepark #skateboard #skatelife')," +
                "('Sport/Active/Travel','Fitness/Sport','#health #fitness #fit #TFLers #fitnessmodel #fitnessaddict #fitspo #workout #bodybuilding #cardio #gym #train #training #photooftheday #health #healthy #instahealth #healthychoices #active #strong #motivation #instagood #determination #lifestyle #diet #getfit #cleaneating #eatclean #excercise')," +
                "('Sport/Active/Travel','Sports','#sports #sport #active #fit #football #soccer #basketball #futball #ball #balls #fun #game #games #crowd #fans #play #playing #player #field #green #grass #score #goal #action #kick #throw #pass #win #winning')," +
                "('Sport/Active/Travel','Dance','#dance #dancer #dancing #dancerecital #music #song #songs #ballet #dancers #dancefloor #danceshoes #instaballet #studio #instadance #instagood #workout #cheer #choreography #flexible #flexibility #photooftheday #love #practice #fun')," +
                "('Urban','Architecture','#architecture #building #architexture #city #buildings #skyscraper #urban #design #minimal #cities #town #street #art #arts #architecturelovers #abstract #lines #instagood #beautiful #archilovers #architectureporn #lookingup #style #archidaily #composition #geometry #geometric #perspective #pattern')," +
                "('Urban','Street Art','#streetart #street #streetphotography #sprayart #urban #urbanart #urbanwalls #wall #wallporn #graffitiigers #stencilart #art #graffiti #instagraffiti #instagood #artwork #mural #graffitiporn #photooftheday #stencil #streetartistry #photography #stickerart #pasteup #instagraff #instagrafite #streetarteverywhere')" );


    }


}
