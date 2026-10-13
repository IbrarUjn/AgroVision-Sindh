# disease_advice.py
# Preventive guidance for all 38 PlantVillage classes
# Languages: English (en), Urdu (ur), Sindhi (sd)

DISEASE_ADVICE = {
    "Apple___Apple_scab": {
        "display_en": "Apple Scab",
        "display_ur": "سیب کا خارش روگ",
        "display_sd": "سيب جو اسڪيب روگ",
        "crop_en": "Apple",
        "crop_ur": "سیب",
        "crop_sd": "سيب",
        "advice_en": (
            "Apply fungicides (captan or myclobutanil) at bud break. "
            "Remove and destroy fallen leaves to reduce spore sources. "
            "Plant resistant apple varieties. Ensure good air circulation by pruning."
        ),
        "advice_ur": (
            "کلی کھلنے کے وقت فنگسائیڈ (کیپٹن یا مائیکلوبیوٹانل) لگائیں۔ "
            "بیضوں کے ذرائع کم کرنے کے لیے گرے ہوئے پتے ہٹا کر تلف کریں۔ "
            "مزاحم سیب کی اقسام لگائیں۔ کاٹ چھانٹ سے ہوا کی آمدورفت یقینی بنائیں۔"
        ),
        "advice_sd": (
            "ڪلي کلڻ وقت فنگيسائيڊ (ڪيپٽن يا مائيڪلوبيوٽانل) لڳايو. "
            "گريل پنن کي هٽائي ختم ڪريو. مزاحم سيب جون قسمون پوکيو. "
            "ڪٽ ڇانٽ سان هوا جي آمد و رفت يقيني بڻايو."
        ),
    },
    "Apple___Black_rot": {
        "display_en": "Apple Black Rot",
        "display_ur": "سیب کی سیاہ سڑن",
        "display_sd": "سيب جي ڪاري سڙڻ",
        "crop_en": "Apple",
        "crop_ur": "سیب",
        "crop_sd": "سيب",
        "advice_en": (
            "Prune out dead or diseased wood and destroy it. "
            "Apply copper-based fungicides during the growing season. "
            "Remove mummified fruits from trees and ground. Avoid overhead irrigation."
        ),
        "advice_ur": (
            "مردہ یا بیمار لکڑی کو کاٹ کر تلف کریں۔ "
            "بڑھوتری کے موسم میں تانبے پر مبنی فنگسائیڈ لگائیں۔ "
            "درختوں اور زمین سے سوکھے پھل ہٹائیں۔ اوپری آبپاشی سے بچیں۔"
        ),
        "advice_sd": (
            "مئي يا بيمار لڪڙي ڪٽي ختم ڪريو. وڌندڙ موسم ۾ تانبي وارو فنگيسائيڊ لڳايو. "
            "سڪل ميوا هٽايو. مٿان پاڻي ڏيڻ کان پاسو ڪريو."
        ),
    },
    "Apple___Cedar_apple_rust": {
        "display_en": "Cedar Apple Rust",
        "display_ur": "سیب کا سیدر زنگ",
        "display_sd": "سيب جو سيڊر زنگ",
        "crop_en": "Apple",
        "crop_ur": "سیب",
        "crop_sd": "سيب",
        "advice_en": (
            "Remove nearby juniper/cedar trees which act as alternate hosts. "
            "Apply myclobutanil or propiconazole fungicide at pink bud stage. "
            "Plant rust-resistant apple varieties."
        ),
        "advice_ur": (
            "قریبی جونیپر/سیدر درخت ہٹائیں جو متبادل میزبان ہیں۔ "
            "گلابی کلی کے مرحلے پر مائیکلوبیوٹانل یا پروپیکونازول فنگسائیڈ لگائیں۔ "
            "زنگ مزاحم سیب کی اقسام کاشت کریں۔"
        ),
        "advice_sd": (
            "ويجها جونيپر/سيڊر وڻ هٽايو. گلابي ڪلي مرحلي تي مائيڪلوبيوٽانل فنگيسائيڊ لڳايو. "
            "زنگ مزاحم سيب قسمون پوکيو."
        ),
    },
    "Apple___Healthy": {
        "display_en": "Healthy Apple",
        "display_ur": "صحت مند سیب",
        "display_sd": "صحتمند سيب",
        "crop_en": "Apple",
        "crop_ur": "سیب",
        "crop_sd": "سيب",
        "advice_en": "Your apple plant appears healthy! Maintain good orchard hygiene, regular pruning, and balanced fertilization to keep it disease-free.",
        "advice_ur": "آپ کا سیب کا پودا صحت مند نظر آتا ہے! صحت مند باغ کی دیکھ بھال، باقاعدہ کٹائی اور متوازن کھاد ڈالیں۔",
        "advice_sd": "توهان جو سيب صحتمند آهي! باغ جي صفائي، باقاعده ڪٽ ڇانٽ ۽ متوازن کاد ڏيندا رهو.",
    },
    "Blueberry___Healthy": {
        "display_en": "Healthy Blueberry",
        "display_ur": "صحت مند بلوبیری",
        "display_sd": "صحتمند بلوبيري",
        "crop_en": "Blueberry",
        "crop_ur": "بلوبیری",
        "crop_sd": "بلوبيري",
        "advice_en": "Blueberry plant is healthy. Maintain acidic soil pH (4.5–5.5), mulch around plants, and water consistently.",
        "advice_ur": "بلوبیری کا پودا صحت مند ہے۔ مٹی کی تیزابیت (pH 4.5–5.5) برقرار رکھیں، ملچنگ کریں اور باقاعدہ پانی دیں۔",
        "advice_sd": "بلوبيري صحتمند آهي. مٽيءَ جو تيزاب (pH 4.5-5.5) برقرار رکو، ملچنگ ڪريو.",
    },
    "Cherry_(including_sour)___Powdery_mildew": {
        "display_en": "Cherry Powdery Mildew",
        "display_ur": "چیری کا سفید پاؤڊری ملدیو",
        "display_sd": "چيري جو اڇو پائوڊري مليو",
        "crop_en": "Cherry",
        "crop_ur": "چیری",
        "crop_sd": "چيري",
        "advice_en": (
            "Apply sulfur or potassium bicarbonate fungicide at first sign. "
            "Improve air circulation by pruning dense canopies. "
            "Avoid excessive nitrogen fertilization. Water at the base of plants."
        ),
        "advice_ur": (
            "پہلی علامت پر گندھک یا پوٹاشیم بائیکاربونیٹ فنگسائیڈ لگائیں۔ "
            "کثیف شاخوں کی کٹائی کر کے ہوا کی آمدورفت بہتر کریں۔ "
            "زیادہ نائٹروجن کھاد سے گریز کریں۔"
        ),
        "advice_sd": (
            "پهرين علامت تي گندهڪ فنگيسائيڊ لڳايو. گهاٽي شاخن کي ڪٽي هوا کي بهتر ڪريو. "
            "زياده نائٽروجن کاد کان پاسو ڪريو."
        ),
    },
    "Cherry_(including_sour)___Healthy": {
        "display_en": "Healthy Cherry",
        "display_ur": "صحت مند چیری",
        "display_sd": "صحتمند چيري",
        "crop_en": "Cherry",
        "crop_ur": "چیری",
        "crop_sd": "چيري",
        "advice_en": "Cherry plant is healthy! Prune after harvest, use balanced fertilizer, and apply dormant oil spray to prevent pests.",
        "advice_ur": "چیری کا پودا صحت مند ہے! کٹائی کے بعد تراش خراش کریں، متوازن کھاد اور حشرات کش تیل اسپرے کریں۔",
        "advice_sd": "چيري صحتمند آهي! ڪٽائي کانپوءِ تراش خراش ڪريو ۽ متوازن کاد ڏيو.",
    },
    "Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot": {
        "display_en": "Corn Gray Leaf Spot",
        "display_ur": "مکئی کا گرے لیف اسپاٹ",
        "display_sd": "مڪئي جو گرے ليف اسپاٽ",
        "crop_en": "Corn (Maize)",
        "crop_ur": "مکئی",
        "crop_sd": "مڪئي",
        "advice_en": (
            "Plant resistant corn hybrids. Practice crop rotation (avoid corn-on-corn). "
            "Till infected residue. Apply foliar fungicides (strobilurins) if disease pressure is high. "
            "Ensure proper plant spacing for air circulation."
        ),
        "advice_ur": (
            "مزاحم مکئی کی اقسام کاشت کریں۔ فصل کی چکر بندی کریں (مکئی کے بعد مکئی سے گریز)۔ "
            "متاثرہ فصل کی باقیات ہل سے دبائیں۔ ضرورت پڑنے پر پتوں پر فنگسائیڈ اسپرے کریں۔"
        ),
        "advice_sd": (
            "مزاحم مڪئي جون قسمون پوکيو. فصل جي گردش ڪريو. متاثر باقيات وارياري سان دٻايو. "
            "ضرورت تي پنن تي فنگيسائيڊ اسپري ڪريو."
        ),
    },
    "Corn_(maize)___Common_rust_": {
        "display_en": "Corn Common Rust",
        "display_ur": "مکئی کا عام زنگ",
        "display_sd": "مڪئي جو عام زنگ",
        "crop_en": "Corn (Maize)",
        "crop_ur": "مکئی",
        "crop_sd": "مڪئي",
        "advice_en": (
            "Plant rust-resistant corn varieties. Scout fields regularly for early detection. "
            "Apply triazole or strobilurin fungicides at early infection stage. "
            "Avoid late planting dates which increase rust risk."
        ),
        "advice_ur": (
            "زنگ مزاحم مکئی کی اقسام کاشت کریں۔ ابتدائی پتہ لگانے کے لیے کھیتوں کا باقاعدہ معائنہ کریں۔ "
            "ابتدائی انفیکشن کے مرحلے پر ٹرائیازول فنگسائیڈ لگائیں۔"
        ),
        "advice_sd": (
            "زنگ مزاحم مڪئي قسمون پوکيو. کيتن جو باقاعده معائنو ڪريو. "
            "شروعاتي انفيڪشن تي ٽرائيازول فنگيسائيڊ لڳايو."
        ),
    },
    "Corn_(maize)___Northern_Leaf_Blight": {
        "display_en": "Corn Northern Leaf Blight",
        "display_ur": "مکئی کا شمالی پتہ جھلساؤ",
        "display_sd": "مڪئي جو اتر وارو پن سڙڻ",
        "crop_en": "Corn (Maize)",
        "crop_ur": "مکئی",
        "crop_sd": "مڪئي",
        "advice_en": (
            "Use resistant or tolerant corn hybrids. Rotate crops to break disease cycle. "
            "Apply fungicides (propiconazole, azoxystrobin) when disease appears. "
            "Manage crop residue through tillage."
        ),
        "advice_ur": (
            "مزاحم مکئی کی اقسام استعمال کریں۔ بیماری کے چکر کو توڑنے کے لیے فصل بدلیں۔ "
            "بیماری ظاہر ہوتے ہی فنگسائیڈ (پروپیکونازول، ایزوکسی اسٹروبن) لگائیں۔"
        ),
        "advice_sd": (
            "مزاحم مڪئي قسمون استعمال ڪريو. فصل گردش ڪريو. "
            "بيماري ظاهر ٿيندي فنگيسائيڊ لڳايو."
        ),
    },
    "Corn_(maize)___Healthy": {
        "display_en": "Healthy Corn",
        "display_ur": "صحت مند مکئی",
        "display_sd": "صحتمند مڪئي",
        "crop_en": "Corn (Maize)",
        "crop_ur": "مکئی",
        "crop_sd": "مڪئي",
        "advice_en": "Corn plant is healthy! Maintain balanced NPK fertilization, proper irrigation, and regular scouting for pests.",
        "advice_ur": "مکئی کا پودا صحت مند ہے! متوازن NPK کھاد، مناسب آبپاشی اور کیڑوں کی نگرانی جاری رکھیں۔",
        "advice_sd": "مڪئي صحتمند آهي! متوازن NPK کاد، مناسب پاڻي ۽ ڪيڙن جي نگراني جاري رکو.",
    },
    "Grape___Black_rot": {
        "display_en": "Grape Black Rot",
        "display_ur": "انگور کی سیاہ سڑن",
        "display_sd": "انگور جي ڪاري سڙڻ",
        "crop_en": "Grape",
        "crop_ur": "انگور",
        "crop_sd": "انگور",
        "advice_en": (
            "Remove and destroy mummified berries and infected canes. "
            "Apply captan, myclobutanil, or mancozeb fungicide from bud break through early fruit development. "
            "Ensure good canopy airflow."
        ),
        "advice_ur": (
            "سوکھی انگوریاں اور متاثرہ شاخیں ہٹا کر تلف کریں۔ "
            "کلی کھلنے سے ابتدائی پھل بننے تک کیپٹن یا مینکوزیب فنگسائیڈ لگائیں۔"
        ),
        "advice_sd": (
            "سڪل انگور ۽ متاثر شاخون هٽائي ختم ڪريو. ڪلي کلڻ کان پوءِ ڪيپٽن فنگيسائيڊ لڳايو."
        ),
    },
    "Grape___Esca_(Black_Measles)": {
        "display_en": "Grape Esca (Black Measles)",
        "display_ur": "انگور کا ایسکا (بلیک میزلز)",
        "display_sd": "انگور جو ايسڪا",
        "crop_en": "Grape",
        "crop_ur": "انگور",
        "crop_sd": "انگور",
        "advice_en": (
            "There is no cure once a vine is infected. Remove and destroy heavily affected vines. "
            "Protect pruning wounds with fungicide paste. Avoid large pruning cuts. "
            "Practice good vineyard hygiene."
        ),
        "advice_ur": (
            "ایک بار متاثر ہونے پر علاج ممکن نہیں۔ شدید متاثرہ بیلیں ہٹائیں۔ "
            "کٹائی کے زخموں کو فنگسائیڈ پیسٹ سے ڈھکیں۔ باغیچے کی صفائی برقرار رکھیں۔"
        ),
        "advice_sd": (
            "هڪ دفعي متاثر ٿيڻ کانپوءِ علاج ممڪن ناهي. شديد متاثر ويل هٽايو. "
            "ڪٽائي جي زخمن کي فنگيسائيڊ پيسٽ سان ڍڪيو."
        ),
    },
    "Grape___Leaf_blight_(Isariopsis_Leaf_Spot)": {
        "display_en": "Grape Leaf Blight",
        "display_ur": "انگور کا پتہ جھلساؤ",
        "display_sd": "انگور جو پن سڙڻ",
        "crop_en": "Grape",
        "crop_ur": "انگور",
        "crop_sd": "انگور",
        "advice_en": (
            "Apply copper-based or mancozeb fungicide preventatively. "
            "Remove affected leaves and destroy them. Improve air circulation in the canopy. "
            "Avoid overhead irrigation."
        ),
        "advice_ur": (
            "تانبے پر مبنی یا مینکوزیب فنگسائیڈ احتیاطاً لگائیں۔ "
            "متاثرہ پتے ہٹا کر تلف کریں۔ چھتری میں ہوا کی آمدورفت بہتر کریں۔"
        ),
        "advice_sd": (
            "تانبي وارو يا مينڪوزيب فنگيسائيڊ احتياطاً لڳايو. "
            "متاثر پنا هٽائي ختم ڪريو. هوا جي آمد و رفت بهتر ڪريو."
        ),
    },
    "Grape___Healthy": {
        "display_en": "Healthy Grape",
        "display_ur": "صحت مند انگور",
        "display_sd": "صحتمند انگور",
        "crop_en": "Grape",
        "crop_ur": "انگور",
        "crop_sd": "انگور",
        "advice_en": "Grape vine is healthy! Maintain proper trellising, balanced fertilization, and monitor regularly for powdery mildew.",
        "advice_ur": "انگور کی بیل صحت مند ہے! مناسب ٹریلسنگ، متوازن کھاد اور پاؤڈری ملدیو کی نگرانی کریں۔",
        "advice_sd": "انگور صحتمند آهي! مناسب ٽريلسنگ ۽ متوازن کاد برقرار رکو.",
    },
    "Orange___Haunglongbing_(Citrus_greening)": {
        "display_en": "Citrus Greening (HLB)",
        "display_ur": "لیموں کا گرینِنگ مرض (HLB)",
        "display_sd": "ليمن جو گرينِنگ مرض",
        "crop_en": "Orange (Citrus)",
        "crop_ur": "مالٹا/لیموں",
        "crop_sd": "مالٽا/ليمن",
        "advice_en": (
            "There is no cure. Remove and destroy infected trees promptly to prevent spread. "
            "Control the Asian citrus psyllid vector with insecticides. "
            "Plant certified disease-free nursery stock only."
        ),
        "advice_ur": (
            "کوئی علاج نہیں۔ پھیلاؤ روکنے کے لیے متاثرہ درخت فوری ہٹائیں۔ "
            "ایشیائی سائٹرس سائیلڈ کیڑے کو کیڑے مار دوا سے کنٹرول کریں۔ "
            "صرف مستند نرسری سے پودے خریدیں۔"
        ),
        "advice_sd": (
            "ڪو علاج ناهي. متاثر وڻ جلدي هٽائي ختم ڪريو. "
            "ايشيائي سائيٽرس سائيلڊ کي ڪيڙ مار دوا سان ڪنٽرول ڪريو."
        ),
    },
    "Peach___Bacterial_spot": {
        "display_en": "Peach Bacterial Spot",
        "display_ur": "آڑو کا بیکٹیریل داغ",
        "display_sd": "آڙو جو بيڪٽيريل داغ",
        "crop_en": "Peach",
        "crop_ur": "آڑو",
        "crop_sd": "آڙو",
        "advice_en": (
            "Apply copper bactericides from petal fall through harvest. "
            "Plant resistant varieties. Prune to improve air circulation. "
            "Avoid overhead sprinkler irrigation. Remove infected tissue."
        ),
        "advice_ur": (
            "پھول گرنے سے کٹائی تک تانبے پر مبنی بیکٹیریاکش اسپرے کریں۔ "
            "مزاحم اقسام لگائیں۔ ہوا کی آمدورفت کے لیے تراش خراش کریں۔"
        ),
        "advice_sd": (
            "گل ڪرڻ کان ڪٽائي تائين تانبي وارو بيڪٽيريا ڪش اسپري ڪريو. "
            "مزاحم قسمون پوکيو. تراش خراش ڪري هوا کي بهتر ڪريو."
        ),
    },
    "Peach___Healthy": {
        "display_en": "Healthy Peach",
        "display_ur": "صحت مند آڑو",
        "display_sd": "صحتمند آڙو",
        "crop_en": "Peach",
        "crop_ur": "آڑو",
        "crop_sd": "آڙو",
        "advice_en": "Peach tree is healthy! Apply dormant copper spray before bud break and maintain proper thinning for quality fruit.",
        "advice_ur": "آڑو کا درخت صحت مند ہے! کلی کھلنے سے پہلے تانبے کا اسپرے کریں اور پھل پتلا کریں۔",
        "advice_sd": "آڙو صحتمند آهي! ڪلي کلڻ کان اڳ تانبي اسپري ڪريو.",
    },
    "Pepper,_bell___Bacterial_spot": {
        "display_en": "Bell Pepper Bacterial Spot",
        "display_ur": "شملہ مرچ کا بیکٹیریل داغ",
        "display_sd": "شملي مرچ جو بيڪٽيريل داغ",
        "crop_en": "Bell Pepper",
        "crop_ur": "شملہ مرچ",
        "crop_sd": "شملي مرچ",
        "advice_en": (
            "Use certified disease-free seed. Apply copper-based bactericide sprays. "
            "Avoid working in field when plants are wet. Practice crop rotation. "
            "Remove infected plant debris."
        ),
        "advice_ur": (
            "مستند بیج استعمال کریں۔ تانبے پر مبنی بیکٹیریاکش اسپرے کریں۔ "
            "پودوں کے گیلے ہونے پر کھیت میں کام سے گریز کریں۔ فصل بدلیں۔"
        ),
        "advice_sd": (
            "مستند ٻج استعمال ڪريو. تانبي وارو بيڪٽيريا ڪش اسپري ڪريو. "
            "پودن جي ڀِڄڻ وقت کيت ۾ ڪم نه ڪريو. فصل بدليو."
        ),
    },
    "Pepper,_bell___Healthy": {
        "display_en": "Healthy Bell Pepper",
        "display_ur": "صحت مند شملہ مرچ",
        "display_sd": "صحتمند شملي مرچ",
        "crop_en": "Bell Pepper",
        "crop_ur": "شملہ مرچ",
        "crop_sd": "شملي مرچ",
        "advice_en": "Bell pepper plant is healthy! Ensure consistent watering, balanced fertilization with calcium to prevent blossom end rot.",
        "advice_ur": "شملہ مرچ کا پودا صحت مند ہے! مستقل پانی اور کیلشیم سمیت متوازن کھاد ڈالیں۔",
        "advice_sd": "شملي مرچ صحتمند آهي! مسلسل پاڻي ۽ ڪيلشيم سان متوازن کاد ڏيو.",
    },
    "Potato___Early_blight": {
        "display_en": "Potato Early Blight",
        "display_ur": "آلو کا ابتدائی جھلساؤ",
        "display_sd": "آلو جو شروعاتي سڙڻ",
        "crop_en": "Potato",
        "crop_ur": "آلو",
        "crop_sd": "آلو",
        "advice_en": (
            "Apply chlorothalonil or mancozeb fungicide preventatively. "
            "Practice crop rotation (3-year cycle). Plant certified seed potatoes. "
            "Avoid overhead irrigation; water at base. Remove and destroy infected leaves."
        ),
        "advice_ur": (
            "احتیاطاً کلوروتھالونل یا مینکوزیب فنگسائیڈ لگائیں۔ "
            "فصل کی چکر بندی (3 سالہ) کریں۔ مستند آلو بیج لگائیں۔"
        ),
        "advice_sd": (
            "احتياطاً ڪلوروٿالونل فنگيسائيڊ لڳايو. فصل گردش (3 سال) ڪريو. "
            "مستند آلو ٻج لڳايو. متاثر پنا هٽايو."
        ),
    },
    "Potato___Late_blight": {
        "display_en": "Potato Late Blight",
        "display_ur": "آلو کا دیر سے جھلساؤ",
        "display_sd": "آلو جو دير وارو سڙڻ",
        "crop_en": "Potato",
        "crop_ur": "آلو",
        "crop_sd": "آلو",
        "advice_en": (
            "Apply systemic fungicides (metalaxyl, cymoxanil) at first sign. "
            "Use blight-resistant potato varieties. Destroy infected haulm before harvest. "
            "Avoid excessive nitrogen. Improve field drainage."
        ),
        "advice_ur": (
            "پہلی علامت پر سیسٹمک فنگسائیڈ (میٹالیکسل) لگائیں۔ "
            "بلائٹ مزاحم آلو کی اقسام استعمال کریں۔ متاثرہ فصل تلف کریں۔"
        ),
        "advice_sd": (
            "پهرين علامت تي سيسٽمڪ فنگيسائيڊ (ميٽاليڪسل) لڳايو. "
            "بلائيٽ مزاحم قسمون استعمال ڪريو. متاثر فصل ختم ڪريو."
        ),
    },
    "Potato___Healthy": {
        "display_en": "Healthy Potato",
        "display_ur": "صحت مند آلو",
        "display_sd": "صحتمند آلو",
        "crop_en": "Potato",
        "crop_ur": "آلو",
        "crop_sd": "آلو",
        "advice_en": "Potato plant is healthy! Use certified disease-free seed, practice rotation, and hill up soil around stems for good tuber development.",
        "advice_ur": "آلو کا پودا صحت مند ہے! مستند بیج، فصل بدلی اور تنوں کے گرد مٹی چڑھانے کا عمل جاری رکھیں۔",
        "advice_sd": "آلو صحتمند آهي! مستند ٻج، فصل گردش ۽ تنن جي چوڌاري مٽي چاڙهيندا رهو.",
    },
    "Raspberry___Healthy": {
        "display_en": "Healthy Raspberry",
        "display_ur": "صحت مند رسبیری",
        "display_sd": "صحتمند ريسبيري",
        "crop_en": "Raspberry",
        "crop_ur": "رسبیری",
        "crop_sd": "ريسبيري",
        "advice_en": "Raspberry is healthy! Prune out old floricanes after harvest, trellis properly, and apply balanced fertilizer in spring.",
        "advice_ur": "رسبیری صحت مند ہے! کٹائی کے بعد پرانی شاخیں ہٹائیں، ٹریلسنگ کریں اور بہار میں کھاد ڈالیں۔",
        "advice_sd": "ريسبيري صحتمند آهي! ڪٽائي کانپوءِ پراڻيون شاخون هٽايو.",
    },
    "Soybean___Healthy": {
        "display_en": "Healthy Soybean",
        "display_ur": "صحت مند سویابین",
        "display_sd": "صحتمند سوئابين",
        "crop_en": "Soybean",
        "crop_ur": "سویابین",
        "crop_sd": "سوئابين",
        "advice_en": "Soybean plant is healthy! Ensure proper spacing, monitor for aphids and whitefly, and inoculate with Bradyrhizobium for nitrogen fixation.",
        "advice_ur": "سویابین کا پودا صحت مند ہے! مناسب فاصلہ رکھیں، تیلے اور سفید مکھی کی نگرانی کریں۔",
        "advice_sd": "سوئابين صحتمند آهي! مناسب فاصلو رکو، تيلن ۽ اڇي مکي جي نگراني ڪريو.",
    },
    "Squash___Powdery_mildew": {
        "display_en": "Squash Powdery Mildew",
        "display_ur": "کدو کا پاؤڈری ملدیو",
        "display_sd": "ڪدو جو پائوڊري مليو",
        "crop_en": "Squash",
        "crop_ur": "کدو",
        "crop_sd": "ڪدو",
        "advice_en": (
            "Apply potassium bicarbonate or sulfur-based fungicide. "
            "Plant resistant varieties. Space plants adequately for airflow. "
            "Remove heavily infected leaves. Avoid wetting foliage."
        ),
        "advice_ur": (
            "پوٹاشیم بائیکاربونیٹ یا گندھک فنگسائیڈ لگائیں۔ "
            "مزاحم اقسام لگائیں۔ ہوا کی آمدورفت کے لیے پودوں کے درمیان مناسب فاصلہ رکھیں۔"
        ),
        "advice_sd": (
            "پوٽاشيم بائيڪاربونيٽ فنگيسائيڊ لڳايو. مزاحم قسمون پوکيو. "
            "هوا لاءِ پودن جي وچ ۾ مناسب فاصلو رکو."
        ),
    },
    "Strawberry___Leaf_scorch": {
        "display_en": "Strawberry Leaf Scorch",
        "display_ur": "اسٹرابیری پتہ جلن",
        "display_sd": "اسٽرابيري پن جلڻ",
        "crop_en": "Strawberry",
        "crop_ur": "اسٹرابیری",
        "crop_sd": "اسٽرابيري",
        "advice_en": (
            "Apply captan or thiram fungicide. Remove and destroy infected leaves. "
            "Avoid overhead irrigation. Plant resistant cultivars. "
            "Renovate beds after harvest by mowing and narrowing rows."
        ),
        "advice_ur": (
            "کیپٹن یا تھیرام فنگسائیڈ لگائیں۔ متاثرہ پتے ہٹائیں۔ "
            "اوپری آبپاشی سے گریز کریں۔ مزاحم اقسام لگائیں۔"
        ),
        "advice_sd": (
            "ڪيپٽن فنگيسائيڊ لڳايو. متاثر پنا هٽايو. مٿان پاڻي ڏيڻ کان پاسو ڪريو. "
            "مزاحم قسمون پوکيو."
        ),
    },
    "Strawberry___Healthy": {
        "display_en": "Healthy Strawberry",
        "display_ur": "صحت مند اسٹرابیری",
        "display_sd": "صحتمند اسٽرابيري",
        "crop_en": "Strawberry",
        "crop_ur": "اسٹرابیری",
        "crop_sd": "اسٽرابيري",
        "advice_en": "Strawberry plant is healthy! Mulch with straw to keep fruit clean, water consistently, and renovate beds annually after harvest.",
        "advice_ur": "اسٹرابیری صحت مند ہے! پھل صاف رکھنے کے لیے بھوسے سے ملچنگ کریں اور کٹائی کے بعد سالانہ تجدید کریں۔",
        "advice_sd": "اسٽرابيري صحتمند آهي! ميوو صاف رکڻ لاءِ ڀوسي سان ملچنگ ڪريو.",
    },
    "Tomato___Bacterial_spot": {
        "display_en": "Tomato Bacterial Spot",
        "display_ur": "ٹماٹر کا بیکٹیریل داغ",
        "display_sd": "ٽماٽو جو بيڪٽيريل داغ",
        "crop_en": "Tomato",
        "crop_ur": "ٹماٹر",
        "crop_sd": "ٽماٽو",
        "advice_en": (
            "Use certified disease-free seeds and transplants. "
            "Apply copper bactericide sprays starting at transplant. "
            "Avoid overhead irrigation. Practice 2-3 year crop rotation. "
            "Remove crop debris after harvest."
        ),
        "advice_ur": (
            "مستند بیج اور پودے استعمال کریں۔ پیوند لگانے سے شروع کرتے ہوئے تانبے کے بیکٹیریاکش اسپرے کریں۔ "
            "اوپری آبپاشی سے گریز کریں۔ 2-3 سال کی فصل بدلی کریں۔"
        ),
        "advice_sd": (
            "مستند ٻج ۽ پودا استعمال ڪريو. تانبي وارو بيڪٽيريا ڪش اسپري ڪريو. "
            "مٿان پاڻي ڏيڻ کان پاسو ڪريو. 2-3 سال فصل بدليو."
        ),
    },
    "Tomato___Early_blight": {
        "display_en": "Tomato Early Blight",
        "display_ur": "ٹماٹر کا ابتدائی جھلساؤ",
        "display_sd": "ٽماٽو جو شروعاتي سڙڻ",
        "crop_en": "Tomato",
        "crop_ur": "ٹماٹر",
        "crop_sd": "ٽماٽو",
        "advice_en": (
            "Apply chlorothalonil or mancozeb fungicide preventatively. "
            "Mulch around plants to reduce soil splash. Practice crop rotation. "
            "Remove lower infected leaves. Stake/cage plants for good air circulation."
        ),
        "advice_ur": (
            "احتیاطاً کلوروتھالونل یا مینکوزیب فنگسائیڈ لگائیں۔ "
            "مٹی کے چھینٹے کم کرنے کے لیے ملچنگ کریں۔ نچلے متاثرہ پتے ہٹائیں۔"
        ),
        "advice_sd": (
            "احتياطاً ڪلوروٿالونل فنگيسائيڊ لڳايو. مٽي جي ڇنڊ کي گهٽائڻ لاءِ ملچنگ ڪريو. "
            "هيٺين متاثر پنن کي هٽايو."
        ),
    },
    "Tomato___Late_blight": {
        "display_en": "Tomato Late Blight",
        "display_ur": "ٹماٹر کا دیر سے جھلساؤ",
        "display_sd": "ٽماٽو جو دير وارو سڙڻ",
        "crop_en": "Tomato",
        "crop_ur": "ٹماٹر",
        "crop_sd": "ٽماٽو",
        "advice_en": (
            "Apply systemic fungicide (metalaxyl-M, cymoxanil) immediately at first sign. "
            "Destroy infected plants; do not compost. Improve drainage. "
            "Avoid overhead irrigation. Plant resistant varieties."
        ),
        "advice_ur": (
            "پہلی علامت پر فوری سیسٹمک فنگسائیڈ (میٹالیکسل) لگائیں۔ "
            "متاثرہ پودے تلف کریں؛ کمپوسٹ نہ کریں۔ نکاسی بہتر کریں۔"
        ),
        "advice_sd": (
            "پهرين علامت تي فوري سيسٽمڪ فنگيسائيڊ لڳايو. "
            "متاثر پودا ختم ڪريو. نيڪاسي بهتر ڪريو."
        ),
    },
    "Tomato___Leaf_Mold": {
        "display_en": "Tomato Leaf Mold",
        "display_ur": "ٹماٹر کا پتہ پھپھوندی",
        "display_sd": "ٽماٽو جي پن فنگس",
        "crop_en": "Tomato",
        "crop_ur": "ٹماٹر",
        "crop_sd": "ٽماٽو",
        "advice_en": (
            "Reduce humidity by improving ventilation (especially in greenhouses). "
            "Apply chlorothalonil or mancozeb fungicide. "
            "Avoid wetting foliage; use drip irrigation. Remove infected leaves."
        ),
        "advice_ur": (
            "ہوا کی آمدورفت بہتر کر کے نمی کم کریں (خاص طور پر گرین ہاؤس میں)۔ "
            "کلوروتھالونل یا مینکوزیب فنگسائیڈ لگائیں۔ ڈرپ آبپاشی استعمال کریں۔"
        ),
        "advice_sd": (
            "هوا جي آمد و رفت بهتر ڪري نمي گهٽ ڪريو. ڪلوروٿالونل فنگيسائيڊ لڳايو. "
            "ڊرپ پاڻي استعمال ڪريو. متاثر پنا هٽايو."
        ),
    },
    "Tomato___Septoria_leaf_spot": {
        "display_en": "Tomato Septoria Leaf Spot",
        "display_ur": "ٹماٹر کا سیپٹوریا پتہ داغ",
        "display_sd": "ٽماٽو جو سيپٽوريا پن داغ",
        "crop_en": "Tomato",
        "crop_ur": "ٹماٹر",
        "crop_sd": "ٽماٽو",
        "advice_en": (
            "Apply mancozeb, chlorothalonil, or copper fungicide at first sign. "
            "Remove infected lower leaves immediately. Mulch to prevent soil splash. "
            "Practice 2-year crop rotation. Stake plants for airflow."
        ),
        "advice_ur": (
            "پہلی علامت پر مینکوزیب یا تانبے کا فنگسائیڈ لگائیں۔ "
            "نچلے متاثرہ پتے فوری ہٹائیں۔ مٹی کے چھینٹوں سے بچاؤ کے لیے ملچنگ کریں۔"
        ),
        "advice_sd": (
            "پهرين علامت تي مينڪوزيب فنگيسائيڊ لڳايو. "
            "هيٺين متاثر پنن کي فوري هٽايو. ملچنگ ڪريو."
        ),
    },
    "Tomato___Spider_mites Two-spotted_spider_mite": {
        "display_en": "Tomato Spider Mites",
        "display_ur": "ٹماٹر کے مکڑی کیڑے",
        "display_sd": "ٽماٽو جا مکڙي ڪيڙا",
        "crop_en": "Tomato",
        "crop_ur": "ٹماٹر",
        "crop_sd": "ٽماٽو",
        "advice_en": (
            "Apply miticide (abamectin, bifenazate) or insecticidal soap. "
            "Increase irrigation; mites thrive in dry conditions. "
            "Introduce predatory mites (Phytoseiulus persimilis). "
            "Avoid dusty field conditions."
        ),
        "advice_ur": (
            "مائٹسائیڈ (ابامیکٹن) یا کیڑے مار صابن لگائیں۔ "
            "آبپاشی بڑھائیں؛ یہ کیڑے خشکی میں پھلتے پھولتے ہیں۔ "
            "شکاری مائٹس کا استعمال کریں۔"
        ),
        "advice_sd": (
            "مائيٽسائيڊ (ابامڪٽن) يا ڪيڙ مار صابڻ لڳايو. پاڻي وڌايو. "
            "شڪاري مائيٽس استعمال ڪريو."
        ),
    },
    "Tomato___Target_Spot": {
        "display_en": "Tomato Target Spot",
        "display_ur": "ٹماٹر ٹارگٹ اسپاٹ",
        "display_sd": "ٽماٽو ٽارگيٽ اسپاٽ",
        "crop_en": "Tomato",
        "crop_ur": "ٹماٹر",
        "crop_sd": "ٽماٽو",
        "advice_en": (
            "Apply chlorothalonil or azoxystrobin fungicide at first sign. "
            "Remove infected leaves. Avoid overhead irrigation. Improve plant spacing for air movement."
        ),
        "advice_ur": (
            "پہلی علامت پر کلوروتھالونل یا ایزوکسی اسٹروبن فنگسائیڈ لگائیں۔ "
            "متاثرہ پتے ہٹائیں۔ اوپری آبپاشی سے گریز کریں۔"
        ),
        "advice_sd": (
            "پهرين علامت تي ڪلوروٿالونل فنگيسائيڊ لڳايو. متاثر پنا هٽايو. "
            "مٿان پاڻي ڏيڻ کان پاسو ڪريو."
        ),
    },
    "Tomato___Tomato_Yellow_Leaf_Curl_Virus": {
        "display_en": "Tomato Yellow Leaf Curl Virus",
        "display_ur": "ٹماٹر زرد پتہ کرل وائرس",
        "display_sd": "ٽماٽو زرد پن ڪرل وائرس",
        "crop_en": "Tomato",
        "crop_ur": "ٹماٹر",
        "crop_sd": "ٽماٽو",
        "advice_en": (
            "No cure for infected plants — remove and destroy them. "
            "Control whitefly vector with insecticides (imidacloprid) or yellow sticky traps. "
            "Plant virus-resistant tomato varieties. Use reflective mulch to repel whiteflies."
        ),
        "advice_ur": (
            "متاثرہ پودوں کا کوئی علاج نہیں — انہیں ہٹا کر تلف کریں۔ "
            "سفید مکھی کو کیڑے مار دوا (امیڈاکلوپرڈ) یا پیلے چپچپے جال سے کنٹرول کریں۔"
        ),
        "advice_sd": (
            "متاثر پودن جو ڪو علاج ناهي — هٽائي ختم ڪريو. "
            "اڇي مکي کي ڪيڙ مار دوا يا پيلي چپچپي جال سان ڪنٽرول ڪريو."
        ),
    },
    "Tomato___Tomato_mosaic_virus": {
        "display_en": "Tomato Mosaic Virus",
        "display_ur": "ٹماٹر موزیک وائرس",
        "display_sd": "ٽماٽو موزيڪ وائرس",
        "crop_en": "Tomato",
        "crop_ur": "ٹماٹر",
        "crop_sd": "ٽماٽو",
        "advice_en": (
            "Remove and destroy infected plants. Wash hands and tools with soap before handling plants. "
            "Use virus-free certified seed. Control aphid vectors. "
            "Plant resistant tomato varieties."
        ),
        "advice_ur": (
            "متاثرہ پودے ہٹا کر تلف کریں۔ پودوں کو ہاتھ لگانے سے پہلے ہاتھ اور اوزار صابن سے دھوئیں۔ "
            "وائرس فری مستند بیج استعمال کریں۔"
        ),
        "advice_sd": (
            "متاثر پودا هٽائي ختم ڪريو. پودن کي هٿ ڏيڻ کان اڳ هٿ ۽ اوزار صابڻ سان ڌوئو. "
            "وائرس فري مستند ٻج استعمال ڪريو."
        ),
    },
    "Tomato___Healthy": {
        "display_en": "Healthy Tomato",
        "display_ur": "صحت مند ٹماٹر",
        "display_sd": "صحتمند ٽماٽو",
        "crop_en": "Tomato",
        "crop_ur": "ٹماٹر",
        "crop_sd": "ٽماٽو",
        "advice_en": "Tomato plant is healthy! Water consistently at the base, use drip irrigation, and provide calcium to prevent blossom end rot.",
        "advice_ur": "ٹماٹر کا پودا صحت مند ہے! تنے کی جڑ میں پانی دیں، ڈرپ آبپاشی استعمال کریں اور کیلشیم فراہم کریں۔",
        "advice_sd": "ٽماٽو صحتمند آهي! تني جي پاڙ ۾ پاڻي ڏيو، ڊرپ پاڻي استعمال ڪريو ۽ ڪيلشيم ڏيو.",
    },
}


def get_advice(class_name: str, lang: str = "en") -> dict:
    """
    Retrieve advice for a given class_name in the requested language.
    Falls back to English if language key is missing.
    Returns a dict with keys: display, crop, advice.
    """
    entry = DISEASE_ADVICE.get(class_name)
    if not entry:
        return {
            "display": class_name.replace("___", " — ").replace("_", " "),
            "crop": class_name.split("___")[0].replace("_", " "),
            "advice": "Consult a local agricultural extension officer for guidance on this condition.",
        }
    return {
        "display": entry.get(f"display_{lang}", entry.get("display_en", class_name)),
        "crop": entry.get(f"crop_{lang}", entry.get("crop_en", "")),
        "advice": entry.get(f"advice_{lang}", entry.get("advice_en", "")),
    }
