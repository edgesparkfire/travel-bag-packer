package com.example.bag.service;

import com.example.bag.model.CityGuide;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CityGuideService {

    private final Map<String, CityGuide> cityDatabase = new HashMap<>();

    public CityGuideService() {
        seedCityDatabase();
    }

    public CityGuide getGuideForCity(String cityQuery) {
        if (cityQuery == null || cityQuery.trim().isEmpty()) {
            return getDefaultGuide("Your Destination");
        }

        String normalized = cityQuery.trim().toLowerCase();
        for (Map.Entry<String, CityGuide> entry : cityDatabase.entrySet()) {
            if (normalized.contains(entry.getKey()) || entry.getKey().contains(normalized)) {
                return entry.getValue();
            }
        }

        return getDefaultGuide(cityQuery);
    }

    private void seedCityDatabase() {
        // HYDERABAD, INDIA
        Map<String, String> hydEmergency = new LinkedHashMap<>();
        hydEmergency.put("Police Control Room", "100 / 112");
        hydEmergency.put("Ambulance (Emergency Medical)", "108");
        hydEmergency.put("Women Helpline (She Teams)", "181 / 1091");
        hydEmergency.put("Tourist Information Helpline", "1363");
        hydEmergency.put("Cyberabad Police WhatsApp", "+91 9490617444");

        Map<String, String> hydPhrases = new LinkedHashMap<>();
        hydPhrases.put("Hello / Greetings", "Namaskaram (Telugu) / Adaab (Urdu) / Namaste");
        hydPhrases.put("Thank you", "Dhanyavadalu (Telugu) / Shukriya (Urdu)");
        hydPhrases.put("How much is this?", "Idhi entha? (Telugu) / Yeh kitne ka hai? (Hindi)");
        hydPhrases.put("Where is the station / hospital?", "Station ekkada undi? (Telugu) / Station kahan hai?");
        hydPhrases.put("Less spicy please", "Karam thakkuva veyandi (Telugu) / Mirchi kam (Hindi)");
        hydPhrases.put("Do you speak English?", "Meeku English vachha? (Telugu) / English aati hai?");

        cityDatabase.put("hyderabad", CityGuide.builder()
                .city("Hyderabad")
                .country("India")
                .generalSummary("The historic City of Pearls and major tech metropolis, renowned for Nizami heritage, majestic Golconda Fort, and world-famous Hyderabadi Biryani.")
                .knowledgeSource("Telangana State Tourism Development Corp (TSTDC) & Cyberabad Police Directorate")
                .bestTimeToVisit("October to March (pleasant 18C-28C; avoid peak summer April-May 42C)")
                .voltageAndSocket("230V, 50Hz. Socket types C, D, and M (3 round pins). Universal adapter recommended.")
                .emergencyContacts(hydEmergency)
                .transportAdvice(Arrays.asList(
                        "Hyderabad Metro Rail (Red, Blue, Green lines) is the fastest air-conditioned transit across Secunderabad, Ameerpet, Hitec City, and Jubilee Hills. Purchase a smart card or QR ticket via WhatsApp/Paytm.",
                        "Auto-rickshaws: Always book via Uber Auto, Ola Auto, or Rapido to avoid inflated off-meter haggling at railway stations and bazaars.",
                        "Railway Stations: For Secunderabad (SC) or Hyderabad Deccan (Nampally), arrive 40 minutes prior to departure. Keep your IRCTC m-ticket and original passport/ID ready for ticket examiner (TTE) check."
                ))
                .rideApps(Arrays.asList("Uber (Cabs & Autos)", "Ola Cabs", "Rapido (Bike Taxi & Auto-rickshaw)"))
                .culturalEtiquette(Arrays.asList(
                        "Modest Dress: Wear conservative attire covering shoulders and knees when visiting historic monuments and places of worship (Mecca Masjid, Charminar, Birla Mandir).",
                        "Footwear Rule: Always remove shoes before stepping into temples, mosques, and traditional Indian homes. Slip-on shoes are easiest.",
                        "Dining & Hand Protocol: Use your right hand for eating, passing food, and handing cash or cards. Avoid using the left hand for interpersonal exchanges.",
                        "Bargaining Etiquette: In Laad Bazaar beside Charminar, respectful bargaining for bangles and pearls is customary (start at 60-70% of quoted price)."
                ))
                .tapWaterStatus("Strictly drink sealed bottled mineral water (trusted brands: Bisleri, Kinley, Aquafina with unbroken tamper seals). Avoid raw tap water and street iced drinks.")
                .tippingCulture("7% to 10% is customary at sit-down restaurants unless a service charge is already printed on the bill. Rounding off auto-rickshaw fares by 20-30 rupees is standard.")
                .commonPhrases(hydPhrases)
                .mustTryDishes(Arrays.asList(
                        "Authentic Hyderabadi Dum Biryani (Paradise, Hotel Shadab, or Cafe Bahar)",
                        "Irani Chai with crisp Osmania Biscuits at Nimrah Cafe overlooking Charminar",
                        "Hyderabadi Haleem (slow-cooked wheat, meat, lentils & spices)",
                        "Mirchi ka Salan & Double ka Meetha (royal saffron bread pudding)"
                ))
                .build());

        // TOKYO, JAPAN
        Map<String, String> tokyoEmergency = new LinkedHashMap<>();
        tokyoEmergency.put("Ambulance / Fire", "119");
        tokyoEmergency.put("Police", "110");
        tokyoEmergency.put("Japan Visitor Helpline", "050-3816-2788");

        Map<String, String> tokyoPhrases = new LinkedHashMap<>();
        tokyoPhrases.put("Hello", "Konnichiwa");
        tokyoPhrases.put("Thank you", "Arigatou gozaimasu");
        tokyoPhrases.put("Excuse me / Sorry", "Sumimasen");
        tokyoPhrases.put("Where is the station?", "Eki wa doko desu ka?");

        cityDatabase.put("tokyo", CityGuide.builder()
                .city("Tokyo")
                .country("Japan")
                .generalSummary("A seamless blend of ultra-modern technology and timeless traditions. Extremely clean, punctual, and safe.")
                .knowledgeSource("Japan National Tourism Organization (JNTO) & Tokyo Metropolitan Gov")
                .bestTimeToVisit("March-May (Cherry Blossoms) or September-November (Autumn Foliage)")
                .voltageAndSocket("100V, 50/60Hz. Type A and B plugs (two flat pins).")
                .emergencyContacts(tokyoEmergency)
                .transportAdvice(Arrays.asList(
                        "Add a digital Suica or Pasmo transit IC card directly to Apple/Google Wallet for all trains, subways, and convenience stores.",
                        "Trains stop operating around midnight; plan transit or budget for late-night taxis.",
                        "Download 'Japan Travel by NAVITIME' for train routing and platform numbers."
                ))
                .rideApps(Arrays.asList("GO (Japan's #1 taxi app)", "Uber", "DiDi"))
                .culturalEtiquette(Arrays.asList(
                        "Keep noise minimal on public trains; phone calls are prohibited inside carriages.",
                        "Stand on the left side of escalators in Tokyo (right side in Osaka).",
                        "Strictly NO tipping. Leaving extra money can be perceived as an accidental overpayment."
                ))
                .tapWaterStatus("Safe to drink: Tokyo tap water is completely potable, clean, and tested daily.")
                .tippingCulture("Zero tipping. Exceptional service is included by default.")
                .commonPhrases(tokyoPhrases)
                .mustTryDishes(Arrays.asList(
                        "Authentic Tonkotsu Ramen",
                        "Fresh Nigiri Sushi at Tsukiji Outer Market",
                        "Crisp Tempura & Matcha Soft Serve"
                ))
                .build());

        // LONDON, UK
        Map<String, String> londonEmergency = new LinkedHashMap<>();
        londonEmergency.put("Emergency Services", "999 / 112");
        londonEmergency.put("Non-emergency Police", "101");

        Map<String, String> londonPhrases = new LinkedHashMap<>();
        londonPhrases.put("Hello", "Hello / Cheers");
        londonPhrases.put("Thank you", "Cheers / Thank you very much");
        londonPhrases.put("Excuse me", "Pardon me");

        cityDatabase.put("london", CityGuide.builder()
                .city("London")
                .country("United Kingdom")
                .generalSummary("A global capital of culture, history, world-renowned museums, and iconic royal landmarks.")
                .knowledgeSource("VisitBritain & Transport for London (TfL)")
                .bestTimeToVisit("May to September (longer daylight and mild temperatures)")
                .voltageAndSocket("230V, 50Hz. Type G plug (three rectangular pins).")
                .emergencyContacts(londonEmergency)
                .transportAdvice(Arrays.asList(
                        "Simply tap contactless debit/credit card or phone at Tube turnstiles; no paper ticket needed.",
                        "Stand on the RIGHT on all Underground escalators; let walking commuters pass on the left.",
                        "Black Cabs accept cards; minicabs must be pre-booked via apps."
                ))
                .rideApps(Arrays.asList("Uber", "Bolt", "Free Now (Black Cabs)"))
                .culturalEtiquette(Arrays.asList(
                        "Queuing (lining up) is strictly respected; never cut in line.",
                        "Major national museums (British Museum, Tate Modern) are free admission."
                ))
                .tapWaterStatus("Safe to drink: Tap water is fully potable across London.")
                .tippingCulture("10-12.5% in sit-down restaurants (often included as optional Service Charge).")
                .commonPhrases(londonPhrases)
                .mustTryDishes(Arrays.asList("Fish & Chips with mushy peas", "Sunday Roast with Yorkshire pudding", "Afternoon Cream Tea"))
                .build());

        // PARIS, FRANCE
        Map<String, String> parisEmergency = new LinkedHashMap<>();
        parisEmergency.put("European Emergency", "112");
        parisEmergency.put("SAMU Ambulance", "15");
        parisEmergency.put("Police", "17");

        Map<String, String> parisPhrases = new LinkedHashMap<>();
        parisPhrases.put("Hello", "Bonjour");
        parisPhrases.put("Thank you", "Merci beaucoup");
        parisPhrases.put("Please", "S'il vous plait");

        cityDatabase.put("paris", CityGuide.builder()
                .city("Paris")
                .country("France")
                .generalSummary("The City of Light, celebrated for art, fashion, gastronomy, and historic boulevards.")
                .knowledgeSource("Atout France (National Tourism Agency) & RATP Paris Transit")
                .bestTimeToVisit("April-June or September-October (mild weather and fewer crowds)")
                .voltageAndSocket("230V, 50Hz. Type C and E plugs (two round pins).")
                .emergencyContacts(parisEmergency)
                .transportAdvice(Arrays.asList(
                        "Paris Metro and RER connect all monuments. Buy Navigo Easy card or digital tickets on phone.",
                        "Validate your ticket before entering the train and keep it until you exit the station.",
                        "Watch out for pickpockets at crowded tourist hubs (Eiffel Tower, Louvre, Montmartre)."
                ))
                .rideApps(Arrays.asList("Uber", "Bolt", "G7 Taxi"))
                .culturalEtiquette(Arrays.asList(
                        "Always say 'Bonjour Madame/Monsieur' upon entering any boutique, cafe, or shop before asking questions.",
                        "Speak in moderate tones in cafes and public transport."
                ))
                .tapWaterStatus("Safe to drink: Parisian tap water is high quality. Order 'une carafe d'eau' for free tap water in restaurants.")
                .tippingCulture("Service is included by law. Leaving 1-2 euros extra for good service is customary.")
                .commonPhrases(parisPhrases)
                .mustTryDishes(Arrays.asList("Fresh Butter Croissants & Baguettes", "Duck Confit", "Steak Frites", "Macarons from Laduree"))
                .build());

        // NEW YORK, USA
        Map<String, String> nycEmergency = new LinkedHashMap<>();
        nycEmergency.put("Emergency Services", "911");
        nycEmergency.put("City Services & Info", "311");

        Map<String, String> nycPhrases = new LinkedHashMap<>();
        nycPhrases.put("Hello", "Hi / How are you doing?");
        nycPhrases.put("Thank you", "Thanks a lot");
        nycPhrases.put("Where is the subway?", "Where's the nearest subway station?");

        cityDatabase.put("new york", CityGuide.builder()
                .city("New York City")
                .country("United States")
                .generalSummary("The premier global metropolis with soaring skyline, Broadway theater, Central Park, and non-stop energy.")
                .knowledgeSource("NYC Tourism + Conventions & Metropolitan Transportation Authority (MTA)")
                .bestTimeToVisit("September-November (crisp autumn) or May-June (spring bloom)")
                .voltageAndSocket("120V, 60Hz. Type A and B plugs (two flat pins).")
                .emergencyContacts(nycEmergency)
                .transportAdvice(Arrays.asList(
                        "Tap credit/debit card or phone with OMNY at all subway turnstiles. Subway runs 24/7.",
                        "Check whether a train is Local (stops every station) or Express (skips minor stations).",
                        "Walking is often faster than vehicle traffic in Midtown Manhattan."
                ))
                .rideApps(Arrays.asList("Uber", "Lyft", "Curb (Yellow Cabs)"))
                .culturalEtiquette(Arrays.asList(
                        "Walk briskly and keep to the right on sidewalks; do not stop abruptly in pedestrian flow.",
                        "Stand to the right on escalators.",
                        "Tipping is customary: 18% to 20% at restaurants, 10-15% for taxi drivers."
                ))
                .tapWaterStatus("Safe to drink: NYC tap water from the Catskill reservoirs is world renowned for purity.")
                .tippingCulture("18% to 20% in sit-down restaurants and bars.")
                .commonPhrases(nycPhrases)
                .mustTryDishes(Arrays.asList("Classic New York Thin Crust Pizza slice", "Pastrami on Rye from Katz's Delicatessen", "Toasted Bagel with Cream Cheese & Lox", "Junior's Cheesecake"))
                .build());
    }

    private CityGuide getDefaultGuide(String city) {
        Map<String, String> defaultEmergency = new LinkedHashMap<>();
        defaultEmergency.put("Universal Emergency", "112 / 911");
        defaultEmergency.put("Local Tourist Assistance", "Inquire at hotel reception or dial 112");

        Map<String, String> defaultPhrases = new LinkedHashMap<>();
        defaultPhrases.put("Hello", "Hello / Good day");
        defaultPhrases.put("Thank you", "Thank you very much");
        defaultPhrases.put("How much is this?", "How much does this cost?");

        return CityGuide.builder()
                .city(city)
                .country("Global Destination")
                .generalSummary("Welcome to " + city + "! Keep digital copies of your passport, booking vouchers, and tickets readily accessible.")
                .knowledgeSource("International Travel Directory & Municipal Tourist Guidelines")
                .bestTimeToVisit("Check local seasonal forecasts before departure")
                .voltageAndSocket("Verify local voltage (typically 110V-240V). Pack a universal multi-socket adapter.")
                .emergencyContacts(defaultEmergency)
                .transportAdvice(Arrays.asList(
                        "Download offline maps (Google Maps / Citymapper) before heading out from Wi-Fi.",
                        "Identify the central railway terminal or airport transit connection upon arrival.",
                        "Prefer licensed metered taxis or verified ride-hailing apps over street touts."
                ))
                .rideApps(Arrays.asList("Uber", "Local Ride App", "Official Airport Taxi Desk"))
                .culturalEtiquette(Arrays.asList(
                        "Observe local clothing customs at monuments and shrines (modest attire covering shoulders and knees is universally safe).",
                        "Learn the basic local greeting for courteous interactions with shopkeepers.",
                        "Split cash and cards between your main luggage and personal daypack."
                ))
                .tapWaterStatus("When in doubt, purchase sealed bottled water with unbroken caps or use hotel filtration dispensers.")
                .tippingCulture("Typically 7% to 10% in sit-down dining unless service charge is included.")
                .commonPhrases(defaultPhrases)
                .mustTryDishes(Arrays.asList("Explore local food markets and dining spots with high local customer turnover"))
                .build();
    }
}
