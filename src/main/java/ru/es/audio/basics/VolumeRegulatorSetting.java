package ru.es.audio.basics;

import ru.es.util.StringUtils;
import javafx.util.StringConverter;

public enum VolumeRegulatorSetting
{
    LowInfiniteUp0(AmpFormulas.deciBellToAmp(0), 0, NormalizingPow.Pow2),
    LowInfiniteUp6(AmpFormulas.deciBellToAmp(6.01), 0, NormalizingPow.Pow2),
    LowInfiniteUp12(AmpFormulas.deciBellToAmp(12), 0, NormalizingPow.Pow2),
    Low6Up6(AmpFormulas.deciBellToAmp(6.0), AmpFormulas.deciBellToAmp(-6), NormalizingPow.NoPow);

    public final double maxAmp;
    public final double minAmp;
    public final NormalizingPow normalizingPow;
    public final StringConverter<Number> stringConverter;

    VolumeRegulatorSetting(double maxAmp, double minAmp, NormalizingPow normalizingPow)
    {
        this.maxAmp = maxAmp;
        this.minAmp = minAmp;
        this.normalizingPow = normalizingPow;
        stringConverter = createNormalizedToDecibelConverter(this);
    }

    public float ampToNormalized(float amp)
    {
        return AmpFormulas.ampValueToNormalizedValue(amp, this);
    }

    private static StringConverter<Number> createNormalizedToDecibelConverter(VolumeRegulatorSetting setting)
    {
        return new StringConverter<Number>()
        {
            boolean allowLie = true;
            @Override
            public String toString(Number normalized)
            {
                double amp = AmpFormulas.normalizedValueToAmpValue(normalized.doubleValue(), setting.minAmp, setting.maxAmp, setting.normalizingPow);
                if (allowLie && Math.abs(1.0 - amp) < 0.01)
                    amp = 1.0;

                double dba = AmpFormulas.ampToDeciBell(amp);

                if (dba == Double.POSITIVE_INFINITY)
                    return "Max";
                else if (dba == Double.NEGATIVE_INFINITY)
                    return "-Inf";

                return  ""+ StringUtils.getNumberWithFixedSizeAfterDot(dba, 2);
            }

            @Override
            public Number fromString(String string)
            {
                return null;
            }
        };
    }
}
