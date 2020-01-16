package ru.es.audio.deviceParameter;

import ru.es.audio.OscType;
import ru.es.util.ListUtils;
import ru.es.util.StringConverters;
import ru.es.util.StringUtils;
import ru.es.util.Words;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

public class ParameterInfos
{
    public static StringConverter<Number> oscSynthTypesConverter = StringConverters.createListItemConverter(OscType.synthTypes);
    public static StringConverter<Number> osc3TypesConverter = StringConverters.createListItemConverter(OscType.osc3Types);

    public static final ParameterInfo oscBalance = new ParameterInfo("Osc Balance", 0, 127, 1,StringConverters.minus64IntConverter);
    public static final ParameterInfo osc1Wave = new ParameterInfo("Osc1Wave", 0, OscType.synthTypes.size()-1, 1, oscSynthTypesConverter);
    public static final ParameterInfo punch = new ParameterInfo("Punch", 0, 127, 1);
    public static final ParameterInfo hardMono = new ParameterInfo("Mono", 0, 1, 1, new StringConverter<Number>() {
        @Override
        public String toString(Number object)
        {
            return object.intValue() == 0 ? "Off" : "On";
        }

        @Override
        public Number fromString(String string)
        {
            return null;
        }
    });

    public static final ParameterInfo bendUpDown = new ParameterInfo("PBend Range", 0, 128, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo bendDown = new ParameterInfo("BendDown", 0, 128, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo controller3 = new ParameterInfo("Controller3", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo modWheel = new ParameterInfo("ModWheel", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo pitchBend = new ParameterInfo("Pitchband", 0, 8192*2, 1,
                                                                 StringConverters.createToIntConverter("", -8192));
    public static final ParameterInfo uniPanSpread = new ParameterInfo("Pan Spread", 0, 127, 1);
    public static final ParameterInfo uniDetune = new ParameterInfo("U Detune", 0, 127, 1);
    public static final ParameterInfo unisson = new ParameterInfo("Unison", 0, 3, 1,
            StringConverters.createToIntConverter("", 1));


    private static final StringConverter<Number> unissonTimeOffsetConv = new StringConverter<Number>() {
        @Override
        public String toString(Number object)
        {
            return StringUtils.getNumberWithFixedSizeAfterDot((object.floatValue() / 127f * object.floatValue() / 127f) / 6f, // pow 2
                    3) + " sec";
        }

        @Override
        public Number fromString(String string)
        {
            return null;
        }
    };
    public static final ParameterInfo unisson1TimeOffset = new ParameterInfo("U1 Time Offset", 0, 127, 1, unissonTimeOffsetConv);
    public static final ParameterInfo unisson2TimeOffset = new ParameterInfo("U2 Time Offset", 0, 127, 1, unissonTimeOffsetConv);
    public static final ParameterInfo unisson3TimeOffset = new ParameterInfo("U3 Time Offset", 0, 127, 1, unissonTimeOffsetConv);
    public static final ParameterInfo unisson4TimeOffset = new ParameterInfo("U4 Time Offset", 0, 127, 1, unissonTimeOffsetConv);

    public static final ParameterInfo portamento = new ParameterInfo("Portamento", 0, 127, 1);
    public static final ParameterInfo veloSensitivity = new ParameterInfo("Velo Sense", 0, 1, 0.01);
    public static final ParameterInfo filterHiSens = new ParameterInfo("Filter Hi Sense", 0, 1, 0.01);
    public static final ParameterInfo ampRelease = new ParameterInfo("Amp Release", 0, 127, 1);
    public static final ParameterInfo ampSlope = new ParameterInfo("Amp Slope", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo ampSustain = new ParameterInfo("Amp Sustain", 0, 127, 1);
    public static final ParameterInfo ampDecay = new ParameterInfo("Amp Decay", 0, 127, 1);
    public static final ParameterInfo ampAttack = new ParameterInfo("Amp Attack", 0, 127, 1);

    public static final ParameterInfo release = new ParameterInfo("Release", 0, 127, 1);
    public static final ParameterInfo slope = new ParameterInfo("Slope", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo sustain = new ParameterInfo("Sustain", 0, 127, 1);
    public static final ParameterInfo decay = new ParameterInfo("Decay", 0, 127, 1);
    public static final ParameterInfo attack = new ParameterInfo("Attack", 0, 127, 1);

    public static final ParameterInfo osc2Wave = new ParameterInfo("Osc2Wave", 0, OscType.synthTypes.size()-1, 1, oscSynthTypesConverter);
    public static final ParameterInfo osc3Wave = new ParameterInfo("Osc3Wave", 0, OscType.osc3Types.size()-1, 1, osc3TypesConverter);

    public static final ParameterInfo attack3 = new ParameterInfo("Attack 3", 0, 127, 1);
    public static final ParameterInfo decay3 = new ParameterInfo("Decay 3", 0, 127, 1);
    public static final ParameterInfo sustain3 = new ParameterInfo("Sustain 3", 0, 127, 1);
    public static final ParameterInfo release3 = new ParameterInfo("Release3 3", 0, 127, 1);
    public static final ParameterInfo slope3 = new ParameterInfo("Slope 3", 0, 127, 1,
            StringConverters.minus64IntConverter);

    public static final ParameterInfo fltAttack = new ParameterInfo("Flt Attack", 0, 127, 1);
    public static final ParameterInfo fltDecay = new ParameterInfo("Flt Decay", 0, 127, 1);
    public static final ParameterInfo fltSustain = new ParameterInfo("Flt Sustain", 0, 127, 1);
    public static final ParameterInfo fltRelease = new ParameterInfo("Flt Release", 0, 127, 1);
    public static final ParameterInfo fltSlope = new ParameterInfo("Flt Slope", 0, 127, 1,
            StringConverters.minus64IntConverter);

    static StringConverter<Number> oscShapeConverter = new StringConverter<Number>() {
        @Override
        public String toString(Number object)
        {
            int val = object.intValue();
            if (val == 0)
                return "Wave 100%";
            else if (val == 64)
                return "Saw 100%";
            else if (val == 127)
                return "Pulse 100%";
            else if (val > 0 && val < 64)
                return "Saw>Wave "+((64-val) * 100 / 64)+"%";
            else// if (val > 64 && val < 127)
                return "Saw>Pulse "+((val-64) * 100 / 63)+"%";
        }

        @Override
        public Float fromString(String string)
        {
            return null;
        }
    };
    public static final ParameterInfo osc1Shape = new ParameterInfo("Osc1Shape", 0, 127, 1, oscShapeConverter);
    public static final ParameterInfo osc2Shape = new ParameterInfo("Osc2Shape", 0, 127, 1, oscShapeConverter);
    public static final ParameterInfo osc1Pw = new ParameterInfo("Osc 1 PW", 0, 127, 1);
    public static final ParameterInfo osc2Pw = new ParameterInfo("Osc 2 PW", 0, 127, 1);

    public static final ParameterInfo osc3Vol = new ParameterInfo("Osc 3 Volume", 0, 127, 1);
    public static final ParameterInfo semitone3 = new ParameterInfo("Semitone 3", 64-48, 64+48, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo semitone1 = new ParameterInfo("Semitone 1", 64-48, 64+48, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo sync1 = new ParameterInfo("Sync 1", 0, 36, 0.2, StringConverters.twoFloatsStringConverter);
    public static final ParameterInfo semitone2 = new ParameterInfo("Semitone 2", 64-48, 64+48, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo detune3 = new ParameterInfo("Detune 3", 0, 127, 1);
    public static final ParameterInfo detune2 = new ParameterInfo("Detune 2", 0, 127, 1);
    public static final ParameterInfo microTune = new ParameterInfo("Micro Tune", 0, 127, 1,
            StringConverters.minus64IntConverter);
    public static final ParameterInfo ringMod = new ParameterInfo("Ring Mod", 0, 127, 1);
    public static final ParameterInfo unissonOsc1Count = new ParameterInfo("Osc 1 Count", 1, 8, 1);
    public static final ParameterInfo voice1TuneSpread = new ParameterInfo("Voice 1 Tune Spread", 0, 127, 1);
    public static final ParameterInfo phaseInit = new ParameterInfo("Phase Init", 0, 127, 1, new StringConverter<Number>() {
        @Override
        public String toString(Number object)
        {
            if (object.intValue() ==0)
                return "Off";
            else
                return ""+object.intValue();
        }

        @Override
        public Number fromString(String string)
        {
            return null;
        }
    });

    public static final ParameterInfo oscSync = new ParameterInfo("Sync", 0, 1, 1,
            StringConverters.createBooleanNormalizedStringConverter("On", "Off"));
    public static final ParameterInfo panorama = new ParameterInfo("Panorama", 0, 127, 1, StringConverters.minus64IntConverter);


    public static StringConverter<Number> keyFollowStringConverter = new StringConverter<Number>() {
        @Override
        public String toString(Number object)
        {
            int kflw = object.intValue()-64;
            if (kflw == 32)
                return "Normal";
            else return "" + kflw;
        }

        @Override
        public Float fromString(String string)
        {
            return null;
        }
    };


    public static final ParameterInfo keyFollow1 = new ParameterInfo("KeyFollow 1", 0, 128, 1, keyFollowStringConverter);
    public static final ParameterInfo keyFollow2 = new ParameterInfo("KeyFollow 2", 0, 128, 1, keyFollowStringConverter);
    public static final ParameterInfo osc2FM = new ParameterInfo("Osc2 FM", 0, 128, 1, 0);

    public enum FMType
    {
        PosTri,
        Tri,
        Osc1Wave,
        Noize
    }

    public static ObservableList<FMType> fmTypes = FXCollections.observableArrayList(FMType.PosTri, FMType.Tri, FMType.Osc1Wave, FMType.Noize );
    public static final ParameterInfo fmMode = new ParameterInfo("FM Mode", 0, fmTypes.size()-1, 1, StringConverters.createListItemConverter(fmTypes));

    public static ObservableList<OscType> subOscAllowed = FXCollections.observableArrayList(ListUtils.createList(OscType.Square, OscType.Triangle));
    public static final ParameterInfo subOscWave = new ParameterInfo("Sub Osc Wave", 0,
            ParameterInfos.subOscAllowed.size()-1, 1, StringConverters.createListItemConverter(subOscAllowed));
    public static final ParameterInfo subOscVol = new ParameterInfo("Sub Osc Volume", 0, 127, 1);

    public static final ParameterInfo noizeVol = new ParameterInfo("Noise", 0, 127, 1);
    public static final ParameterInfo noizeColor = new ParameterInfo("Noize Color", 0, 127, 1, StringConverters.minus64IntConverter);

    public static final ParameterInfo saturation = new ParameterInfo("Osc Vol", 0, 128, 1,
            new StringConverter<Number>() {
                @Override
                public String toString(Number object)
                {
                    int val = object.intValue();
                    if (val <= 64)
                        return "Vol "+(val*100 / 64)+"";
                    else
                        return "Saturation "+((val-64)*100/63);
                }

                @Override
                public Number fromString(String string)
                {
                    return null;
                }
            });

    public static final ParameterInfo globalTranspose = new ParameterInfo("Transpose", 64-48, 64+48, 1, StringConverters.minus64IntConverter);


    public static final ParameterInfo cutoffLink = new ParameterInfo("Cutoff Link", 0, 1, 1,
            StringConverters.createBooleanNormalizedStringConverter("On", "Off"));

    public static final ParameterInfo filterBalance = new ParameterInfo("Filter Balance", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo keyFollowBase = new ParameterInfo("Key Follow Base", 0, 127, 1, new StringConverter<Number>() {
        @Override
        public String toString(Number object)
        {
            return Words.getNoteName(object.intValue(), true);
        }

        @Override
        public Number fromString(String string)
        {
            return null;
        }
    });


    public static final ParameterInfo cutoff1 = new ParameterInfo("Cutoff 1", 0, 127, 1);
    public static final ParameterInfo reso1 = new ParameterInfo("Reso 1", 0, 127, 1);
    public static final ParameterInfo reso2 = new ParameterInfo("Reso 2", 0, 127, 1);
    public static final ParameterInfo fltEnv1 = new ParameterInfo("Flt Env Amount 1", 0, 127, 1);
    public static final ParameterInfo fltEnv2 = new ParameterInfo("Flt Env Amount 2", 0, 127, 1);
    public static final ParameterInfo fltFollow1 = new ParameterInfo("Flt Key Follow 1", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo fltFollow2 = new ParameterInfo("Flt Key Follow 2", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo fltPolarity1 = new ParameterInfo("Positive Env 1", 0, 1, 1,
            StringConverters.createBooleanNormalizedStringConverter("Negative", "Positive"));
    public static final ParameterInfo fltPolarity2 = new ParameterInfo("Positive Env 2", 0, 1, 1,
            StringConverters.createBooleanNormalizedStringConverter("Negative", "Positive"));

    public static final ParameterInfo lfo1Rate = new ParameterInfo("Lfo 1 Rate", 0, 127, 1);
    public static final ParameterInfo lfo2Rate = new ParameterInfo("Lfo 2 Rate", 0, 127, 1);
    public static final ParameterInfo lfo3Rate = new ParameterInfo("Lfo 3 Rate", 0, 127, 1);

    public static StringConverter<Number> trigPhaseConverter = new StringConverter<Number>() {
        @Override
        public String toString(Number object)
        {
            if (object.intValue() == 0)
                return "Off";

            return ""+object.intValue();
        }

        @Override
        public Number fromString(String string)
        {
            return null;
        }
    };

    public static final ParameterInfo lfo1TrigPhase = new ParameterInfo("Lfo 1 Trig Phase", 0, 127, 1, trigPhaseConverter);
    public static final ParameterInfo lfo2TrigPhase = new ParameterInfo("Lfo 2 Trig Phase", 0, 127, 1, trigPhaseConverter);
    public static final ParameterInfo lfo1Contour = new ParameterInfo("Lfo 1 Contour", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo lfo2Contour = new ParameterInfo("Lfo 2 Contour", 0, 127, 1, StringConverters.minus64IntConverter);

    public static final ParameterInfo lfo1ToOsc1 = new ParameterInfo("Lfo 1 > Osc 1", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo lfo1ToWavePitch = new ParameterInfo("Lfo 1 > Pitch", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo lfo1ToOsc2 = new ParameterInfo("Lfo 1 > Osc 2", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo lfo1ToPw12 = new ParameterInfo("Lfo 1 > PW 1/2", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo lfo1ToReso12 = new ParameterInfo("Lfo 1 > Reso 1/2", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo lfo1ToAssign = new ParameterInfo("Lfo 1 > Assign", 0, 127, 1, StringConverters.minus64IntConverter);

    public static final ParameterInfo lfo2ToFm = new ParameterInfo("Lfo 2 > FM", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo lfo2ToOscShape12 = new ParameterInfo("Lfo 2 > Osc Shape 1/2", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo lfo2ToPan = new ParameterInfo("Lfo 2 > Pan", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo lfo2ToCutoff2 = new ParameterInfo("Lfo 2 > Cutoff 2", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo lfo2ToCutoff1 = new ParameterInfo("Lfo 2 > Cutoff 1", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo lfo2ToAssign = new ParameterInfo("Lfo 2 > Assign", 0, 127, 1, StringConverters.minus64IntConverter);

    //public static final ParameterInfo lfo3ToAssign = new ParameterInfo("Lfo 3 > Assign", 0, 127, 1, StringConverters.minus64IntConverter);

    public static final ParameterInfo matrix11Amt = new ParameterInfo("Matrix 11 Amount", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo matrix12Amt = new ParameterInfo("Matrix 12 Amount", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo matrix13Amt = new ParameterInfo("Matrix 13 Amount", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo matrix21Amt = new ParameterInfo("Matrix 21 Amount", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo matrix22Amt = new ParameterInfo("Matrix 22 Amount", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo matrix23Amt = new ParameterInfo("Matrix 23 Amount", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo matrix31Amt = new ParameterInfo("Matrix 31 Amount", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo matrix32Amt = new ParameterInfo("Matrix 32 Amount", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo matrix33Amt = new ParameterInfo("Matrix 33 Amount", 0, 127, 1, StringConverters.minus64IntConverter);


    public static final ParameterInfo veloToVol = new ParameterInfo("Velo>Vol", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo veloToFM = new ParameterInfo("Velo>FM", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo veloToFlt1EnvAmt = new ParameterInfo("Velo>Flt1EnvAmount", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo veloToFlt2EnvAmt = new ParameterInfo("Velo>Flt2EnvAmount", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo veloToReso1 = new ParameterInfo("Velo>Reso1", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo veloToReso2 = new ParameterInfo("Velo>Reso2", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo postFilterVolume = new ParameterInfo("Post Filter Volume", 0, 127, 1);

    public static final ParameterInfo fltEnvToPitch2 = new ParameterInfo("Flt Env > Pitch2", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo fltEnvToFm = new ParameterInfo("Flt Env > FM", 0, 127, 1, StringConverters.minus64IntConverter);

    public static final ParameterInfo eqLowFreq = new ParameterInfo("EQLowFreq", 0, 127, 1);
    public static final ParameterInfo eqMidFreq = new ParameterInfo("EQMidFreq", 0, 127, 1);
    public static final ParameterInfo eqHiFreq = new ParameterInfo("EQHiFreq", 0, 127, 1);
    public static final ParameterInfo eqLowGain = new ParameterInfo("EQLowGain", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo eqMidGain = new ParameterInfo("EQMidGain", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo eqHiGain = new ParameterInfo("EQHiGain", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo eqMidQ = new ParameterInfo("EqualMidQ", 0, 127, 1);

    public static final ParameterInfo patchVol = new ParameterInfo("Patch Volume", 0, 127, 1, StringConverters.minus64IntConverter);

    public static final ParameterInfo distortAmt = new ParameterInfo("Distort Amount", 0, 127, 1);
    public static final ParameterInfo distortMix = new ParameterInfo("Distort Mix", 0, 127, 1);
    public static final ParameterInfo distortHiCut = new ParameterInfo("Dis High Cut", 0, 127f, 1f);

    public static final ParameterInfo phaserMix = new ParameterInfo("Phaser Mix", 0, 127, 1);
    public static final ParameterInfo phaserFreq = new ParameterInfo("Phaser Freq", 0, 127, 1);
    public static final ParameterInfo phaserFeedback = new ParameterInfo("Phaser Feedback", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo phaserStages = new ParameterInfo("Phaser Stages", 0, 5, 1, StringConverters.createToIntConverter("", +1));
    public static final ParameterInfo phaserSpread = new ParameterInfo("Phaser Spread", 0, 127, 1);
    public static final ParameterInfo phaserRate = new ParameterInfo("Phaser Rate", 0, 127, 1);
    public static final ParameterInfo phaserDepth = new ParameterInfo("Phaser Depth", 0, 127, 1);

    public static final ParameterInfo chorusMix =  new ParameterInfo("Chorus Mix", 0, 127, 1);
    public static final ParameterInfo chorusFeedback =  new ParameterInfo("Chorus Feedback", 0, 127, 1, StringConverters.minus64IntConverter);
    public static final ParameterInfo chorusDelay =  new ParameterInfo("Chorus Delay", 0, 127, 1);
    public static final ParameterInfo chorusRate = new ParameterInfo("Chorus Rate", 0, 127, 1);
    public static final ParameterInfo chorusDepth = new ParameterInfo("Chorus Depth", 0, 127, 1);
    public static final ParameterInfo chorusWave =  new ParameterInfo("Chorus Wave", 0, OscType.delayTypes.size()-1, 1,
            StringConverters.createListItemConverter(OscType.delayTypes));
    public static final ParameterInfo chorusCount = new ParameterInfo("Chorus Count", 1, 6, 1);
    public static final ParameterInfo chorusRateOffset = new ParameterInfo("Chorus Rate Offset", 0, 127, 1);

    public static final ParameterInfo delayMix =  new ParameterInfo("Delay Mix", "Mix",0, 127, 1,
            StringConverters.percent127);
    public static final ParameterInfo delayFeedback = new ParameterInfo("Delay Feedback", "Feedback",0, 127, 1,
            StringConverters.percent127);
    public static final ParameterInfo delayTime =  new ParameterInfo("Delay Time","Time", 0, 127, 1,
            StringConverters.percent127);
    public static final ParameterInfo delayRate = new ParameterInfo("Delay Rate","Rate", 0, 127, 1,
            StringConverters.percent127);
    public static final ParameterInfo delayDepth =  new ParameterInfo("Delay Depth", "Depth", 0, 127, 1,
            StringConverters.percent127);
    public static final ParameterInfo delayLfoWave =  new ParameterInfo("Delay LFO Wave", 0, OscType.delayTypes.size()-1, 1,
            StringConverters.createListItemConverter(OscType.delayTypes));
    public static final ParameterInfo delayColor =  new ParameterInfo("Delay Color", "Color", 0, 127, 1,
            StringConverters.percent127m64);
    public static final ParameterInfo delayStereo = new ParameterInfo("Delay Stereo", "Stereo", 0, 127, 1,
            StringConverters.percent127);
    public static final ParameterInfo delayColorReso =  new ParameterInfo("Delay Color Reso", "Reso",0, 127, 1,
            StringConverters.percent127);

    public static final ParameterInfo reverbMix = new ParameterInfo("Reverb Mix", "Mix",0, 127, 1,
            StringConverters.percent127);
    public static final ParameterInfo reverbRoomSize = new ParameterInfo("Reverb Room Size", "Room Size",0, 127, 1,
            StringConverters.percent127);
    public static final ParameterInfo reverbPreDelay = new ParameterInfo("Reverb Pre-delay", "Pre-delay", 0, 127, 1,
            StringConverters.percent127);
    public static final ParameterInfo reverbDamp = new ParameterInfo("Reverb Damp", "Damp", 0, 127, 1,
            StringConverters.percent127);
    public static final ParameterInfo reverbColor = new ParameterInfo("Reverb Color", "Color", 0, 127, 1,
            StringConverters.percent127m64);
    public static final ParameterInfo reverbStereo = new ParameterInfo("Reverb Stereo", "Stereo",0, 127, 1,
            StringConverters.percent127);
    public static final ParameterInfo reverbReturn = new ParameterInfo("Reverb Vol", "Effect Vol", 0, 127, 1,
            StringConverters.percent127);



}