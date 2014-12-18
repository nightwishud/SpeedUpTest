package cn.edu.pku.plde.test.rec;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public enum SmpEnumTest implements SmpEnumInter{
	ZERO_NORM_FOR_ROTATION_DEFINING_VECTOR("zero norm for rotation defining vector"),
    ZERO_NOT_ALLOWED("zero not allowed here");

	private final String sourceFormat;
	
    private SmpEnumTest(final String sourceFormat) {
        this.sourceFormat = sourceFormat;
    }
	
	@Override
	public String getSourceString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalizedString(Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}

}
