package cn.edu.pku.plde.test.rec;

import java.io.Serializable;
import java.util.Locale;

public interface SmpEnumInter extends Serializable{
	String getSourceString();
	String getLocalizedString(Locale locale);
}
