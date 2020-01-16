package ru.es.audio.basics;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.jfx.binding.ESProperty;
import ru.es.math.ESMath;
import ru.es.util.StringUtils;
import javafx.util.StringConverter;

/**
 * Created by saniller on 24.05.2017.
 */
public class AmpFormulas
{
    public static StringConverter<Number> ampToDBConverter = createAmpToDecibelConverter();

    // 2.0 == 6db
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double normalizedValueToAmpValue(double normalizedValue, double minAmp, double maxAmp, NormalizingPow normalizingPow)
    {
        // иногда потребуется это: ampValue = ESMath.specialRound(ampValue, 0.01);
        return ESMath.linearToPowed(minAmp, maxAmp, 1, normalizedValue, normalizingPow.pow);
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static float ampValueToNormalizedValue(double ampValue, double minAmp, double maxAmp, NormalizingPow normalizingPow)
    {
        return (float) ESMath.linearToPowed(0, 1, minAmp,maxAmp, ampValue, normalizingPow.revertPow);
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static float ampValueToNormalizedValue(double ampValue, VolumeRegulatorSetting setting)
    {
        return (float) ESMath.linearToPowedNormalized(setting.minAmp,setting.maxAmp,
                ampValue, setting.normalizingPow.revertPow);
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double ampToDeciBell(double amplitude)
    {
        return 20 * Math.log10(amplitude);
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double deciBellToAmp(double db)
    {
        // log.α b=c и b=a^c
        return Math.pow(10.0, db / 20.0);
    }

    public static double parameterToAmp(double minOutputAmp, double maxOutputAmp, double maxInput, double value, NormalizingPow pow)
    {
        return ESMath.linearToPowed(minOutputAmp, maxOutputAmp, maxInput, value, pow.pow);
    }

    public static double parameterToAmp(double minOutputAmp, double maxOutputAmp, double maxInput, double value)
    {
        return ESMath.linearToPowed(minOutputAmp, maxOutputAmp, maxInput, value, NormalizingPow.Pow2.pow);
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static float parameterToAmp(double minOutputAmp, double maxOutputAmp, double maxInput, double value, double pow)
    {
        return (float) ESMath.linearToPowed(minOutputAmp, maxOutputAmp, maxInput, value, pow);
    }



    public static StringConverter<Number> createAmpToDecibelConverter()
    {
        return new StringConverter<Number>()
        {
            @Override
            public String toString(Number amplitude)
            {
                String db = ""+ StringUtils.getNumberWithFixedSizeAfterDot(AmpFormulas.ampToDeciBell(amplitude.floatValue()), 2);
                return db;
            }

            @Override
            public Number fromString(String string)
            {
                return null;
            }
        };
    }


    public static StringConverter<Number> createNormalizedToDecibelConverter(double minAmp, double maxAmp, boolean allowLie, NormalizingPow normalizingPow)
    {
        return new StringConverter<Number>()
        {
            @Override
            public String toString(Number normalized)
            {
                double amp = AmpFormulas.normalizedValueToAmpValue(normalized.doubleValue(), minAmp, maxAmp, normalizingPow);
                if (allowLie && Math.abs(1.0 - amp) < 0.01)
                    amp = 1.0;

                double dba = AmpFormulas.ampToDeciBell(amp);

                if (dba == Double.POSITIVE_INFINITY)
                    return "Max";
                else if (dba == Double.NEGATIVE_INFINITY)
                    return "-Inf";

                return  ""+StringUtils.getNumberWithFixedSizeAfterDot(dba, 2);
            }

            @Override
            public Number fromString(String string)
            {
                return null;
            }
        };
    }

    /**
     * Created by saniller on 24.05.2017.
     */
    public static class AmpCalc extends ESCalculateFloat
    {
        public static final double SYNTH_POW = 3.0;
        public static final double EQ_POW = 2.0;
        public static final double POW_2 = 2.0;
        public static final double THRESHOLD_POW = 2.0;

        double maxInput;
        double maxAmp;
        double pow;
        double minAmp;

        public AmpCalc(ESProperty<Float> input, double maxInput, double minAmp, double maxAmp, double pow)
        {
            super(input);
            this.maxInput = maxInput;
            this.maxAmp = maxAmp;
            this.pow = pow;
            this.minAmp = minAmp;
        }

        @Override
        @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
        public float calcOutput(float newValue)
        {
            return parameterToAmp(minAmp, maxAmp, maxInput, newValue, pow);
        }
    }
}
