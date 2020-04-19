/*

	Copyright 2017 Danny Kunz

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.


*/
package org.omnaest.svg.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.batik.apps.rasterizer.DestinationType;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;
import org.apache.fop.svg.PDFTranscoder;

/**
 * Helps to convert svg into pdf
 * 
 * @see #toPDF(String)
 * @see #toPDFResult(String)
 * @author omnaest
 */
public class SVGPDFUtils
{

    /**
     * Returns a {@link Function} which maps an svg {@link String} to a pdf byte result
     * 
     * @return
     */
    public static Function<String, byte[]> newSVGToPDFMapper()
    {
        return svg -> toPDF(svg);
    }

    /**
     * Result of a pdf data generation
     * 
     * @see #get()
     * @see #writeTo(File)
     * @author omnaest
     */
    public static interface PDFResult
    {
        /**
         * Returns the pdf data as {@link Byte} array
         * 
         * @return
         */
        public byte[] get();

        /**
         * Writes the pdf data to the given {@link File}
         * 
         * @param file
         * @return
         * @throws IOException
         */
        public PDFResult writeTo(File file) throws IOException;

    }

    /**
     * Wrapper around a PNG image data result
     * 
     * @author omnaest
     * @see #writeTo(File)
     * @see #get()
     */
    public static interface PNGResult
    {
        /**
         * Returns the raw bytes of the PNG image
         * 
         * @return
         */
        public byte[] get();

        /**
         * Writes the PNG image to the given {@link File}
         * 
         * @param file
         * @return
         */
        public PNGResult writeTo(File file);
    }

    /**
     * Converts the given svg {@link String} into a pdf
     * 
     * @see #toPDFResult(String)
     * @param svg
     * @return
     */
    public static byte[] toPDF(String svg)
    {
        //	return convert(svg, DestinationType.PDF); 
        return convert2(svg, new PDFTranscoder());
    }

    public static Supplier<byte[]> toPDFSupplier(Supplier<String> svg)
    {
        return () -> toPDF(svg.get());
    }

    public static Supplier<byte[]> toPDFSupplier(String svg)
    {
        return () -> toPDF(svg);
    }

    /**
     * Converts the given svg {@link String} into a {@link PDFResult}
     * 
     * @param svg
     * @return
     */
    public static PDFResult toPDFResult(String svg)
    {
        return new PDFResult()
        {
            private byte[] data = toPDF(svg);

            @Override
            public PDFResult writeTo(File file) throws IOException
            {
                FileUtils.writeByteArrayToFile(file, this.data);
                return this;
            }

            @Override
            public byte[] get()
            {
                return this.data;
            }

        };
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

        }
        catch (TranscoderException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            try
            {
                ostream.flush();
                ostream.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException();
            }
        }
        return ostream.toByteArray();
    }

    /**
     * Converts a given svg xml {@link String} to a PNG image
     * 
     * @param svg
     * @return
     */
    public static PNGResult toPNGResult(String svg)
    {
        //
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();

        // 
        try
        {
            PNGTranscoder transcoder = new PNGTranscoder();
            TranscoderInput input = new TranscoderInput(new StringReader(svg));
            TranscoderOutput output = new TranscoderOutput(ostream);

            transcoder.transcode(input, output);

        }
        catch (TranscoderException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            try
            {
                ostream.flush();
                ostream.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException();
            }
        }
        byte[] data = ostream.toByteArray();
        return new PNGResult()
        {

            @Override
            public PNGResult writeTo(File file)
            {
                try
                {
                    FileUtils.writeByteArrayToFile(file, data);
                }
                catch (IOException e)
                {
                    throw new IllegalStateException(e);
                }
                return this;
            }

            @Override
            public byte[] get()
            {
                return data;
            }
        };
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

        }
        catch (SVGConverterException | IOException | InterruptedException e)
        {
            throw new RuntimeException(e);
        }

        return retval;
    }
}
