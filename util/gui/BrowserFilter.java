package util.gui;

import java.io.File;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.filechooser.FileFilter;

public class BrowserFilter extends FileFilter {

	private String prefix;
	private String description;
	protected final TreeSet extensions = new TreeSet();

	protected BrowserFilter(String aPrefix, String[] extList) {
		setExtensions(extList);
		setPrefix(aPrefix);
	}

	public static final BrowserFilter createFilter(
		String aPrefix,
		String[] extList) {
		return new BrowserFilter(aPrefix, extList);
	}

	private final void setExtensions(String[] exts) {
		final int max = exts.length;
		for (int i = 0; i < max; i++) {
			extensions.add(exts[i]);
		}
	}

	public void addExtensions(String[] exts) {
		final int len = exts.length;
		for (int i = 0; i < len; i++) {
			extensions.add(exts[i]);
		}
		updateDescription();
	}

	public final boolean accept(File f) {
		boolean accept = doAccept(f);
		return accept;
	}

	public final String getFirstExtension() {
		return (String) extensions.iterator().next();
	}

	protected boolean doAccept(File f) {
		if (f.isDirectory())
			return true;
		String name = f.getName();
		String ext = null;
		final int len = name.length();
		Iterator it = extensions.iterator();
		while (it.hasNext()) {
			ext = (String) it.next();
			int extLen = ext.length();
			if (len > extLen) {
				if (name.substring(len - extLen).equalsIgnoreCase(ext))
					return true;
			}
		}
		return false;
	}

	public final String getDescription() {
		return description;
	}

	public final void setPrefix(String newPrefix) {
		prefix = newPrefix;
		updateDescription();
	}

	private final void updateDescription() {
		StringBuffer buf = new StringBuffer(prefix);
		buf.append("  (");
		Iterator it = extensions.iterator();
		while (it.hasNext()) {
			buf.append(" *");
			buf.append(it.next().toString());
		}
		buf.append(" )");
		description = buf.toString();
	}

}
