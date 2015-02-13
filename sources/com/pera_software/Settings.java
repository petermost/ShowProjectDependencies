package com.pera_software;

import java.io.*;
import java.util.*;
import org.kohsuke.args4j.*;
import com.pera_software.aidkit.lang.*;

//##################################################################################################

public final class Settings {

	private static class Attributes {
		@Option( name = "-v", aliases = "--verbose", usage = "show more verbose messages" )
		private boolean isVerbose = false;

		@Argument( metaVar = "solutionFileName...", required = true, usage = "the solution file name(s)" )
		private List< String > solutionFileNames = new ArrayList<>();
	}

	private static Attributes _attributes = new Attributes();
	private static CmdLineParser _commandLineParser = new CmdLineParser( _attributes );

	//==============================================================================================

	Settings() {
	}

	//==============================================================================================

	public static boolean parseCommandLine( String commandLineArguments[] ) {
		try {
			_commandLineParser.parseArgument( commandLineArguments );
			return true;
		} catch ( CmdLineException exception ) {
			return false;
		}
	}

	//==============================================================================================

	public static boolean isVerbose() {
		return _attributes.isVerbose;
	}

	//==============================================================================================

	public static List< String > solutionFileNames() {
		return _attributes.solutionFileNames;
	}

	//==============================================================================================

	public static List< String > usage() {
		StringWriter usageWriter = new StringWriter();
		_commandLineParser.printUsage( usageWriter, null );
		String usage = usageWriter.toString();
		return Arrays.asList( usage.split( SystemProperties.getLineSeparator() ));
	}
}
