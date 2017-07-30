package org.omnaest.svg.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.apache.batik.apps.rasterizer.DestinationType;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.commons.io.FileUtils;
import org.apache.fop.svg.PDFTranscoder;

public class SVGPDFUtils
{

	public static byte[] toPDF(String svg)
	{
		//	return convert(svg, DestinationType.PDF); 
		return convert2(svg, new PDFTranscoder());
	}

	@Deprecated
	private static byte[] toJPG(String svg)
	{
		//return new byte[0];
		//	return convert(svg, DestinationType.JPEG); 
		return convert2(svg, new JPEGTranscoder());
	}

	private static byte[] convert2(String svg, Transcoder transcoder)
	{
		//
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();

		// 
		try
		{

			transcoder.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, 0.9f);
			transcoder.addTranscodingHint(JPEGTranscoder.KEY_XML_PARSER_VALIDATING, false);
			TranscoderInput input = new TranscoderInput(new StringReader(svg));
			TranscoderOutput output = new TranscoderOutput(ostream);

			transcoder.transcode(input, output);

		} catch (TranscoderException e)
		{
			throw new RuntimeException(e);
		} finally
		{
			try
			{
				ostream.flush();
				ostream.close();
			} catch (IOException e)
			{
				throw new RuntimeException();
			}
		}
		return ostream.toByteArray();
	}

	private static byte[] convert(String svg, DestinationType destinationType)
	{
		byte[] retval = null;
		try
		{
			File tempDirectory = FileUtils.getTempDirectory();

			//
			File intermediateFile = new File(tempDirectory, "intermediate.svg");
			File outputDirectory = new File(tempDirectory, "intermediate.out");

			FileUtils.deleteQuietly(intermediateFile);
			FileUtils.deleteQuietly(outputDirectory);
			FileUtils.forceMkdir(outputDirectory);

			FileUtils.write(intermediateFile, svg, "utf-8");
			System.out.println(outputDirectory.getAbsolutePath());

			//
			SVGConverter converter = new SVGConverter();

			converter.setDestinationType(destinationType);
			converter.setSources(new String[] { intermediateFile.getAbsolutePath() });

			converter.setDst(outputDirectory);
			converter.execute();

			//
			Thread.sleep(100);
			File outputfile = outputDirectory.isDirectory() ? outputDirectory.listFiles()[0] : outputDirectory;
			retval = FileUtils.readFileToByteArray(outputfile);

			//
			FileUtils.forceDelete(outputDirectory);
			FileUtils.forceDelete(intermediateFile);

		} catch (SVGConverterException | IOException | InterruptedException e)
		{
			throw new RuntimeException(e);
		}

		return retval;
	}
}
