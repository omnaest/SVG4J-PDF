/*******************************************************************************
 * Copyright 2021 Danny Kunz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;
import org.omnaest.svg.SVGDrawer;
import org.omnaest.svg.SVGUtils;
import org.omnaest.svg.elements.composite.SVGTextBox;

public class SVGPDFUtilsTest
{
    @Test
    @Ignore
    public void test() throws MalformedURLException, IOException
    {
        String svg = Files.readAllLines(new File("C:/Temp/svgAnkerTest.svg").toPath(), StandardCharsets.UTF_8)
                          .stream()
                          .collect(Collectors.joining());
        byte[] pdf = SVGPDFUtils.toPDF(svg);
        Files.write(new File("C:/Temp/svgAnkerTest.pdf").toPath(), pdf, StandardOpenOption.CREATE);
    }

    @Test
    @Ignore
    public void testTextBox() throws MalformedURLException, IOException
    {
        SVGDrawer drawer = SVGUtils.getDrawer(1000, 800);
        drawer.newBoundedArea()
              .withScalingHeight(100)
              .withScalingWidth(100)
              .add(new SVGTextBox(0, 0, 100, 20, "This is a test").setBorderSize(2)
                                                                  .setBorderColor("black"));
        String svg = drawer.render();
        byte[] pdf = SVGPDFUtils.toPDF(svg);
        Files.write(new File("C:/Temp/svgTextBoxTest.pdf").toPath(), pdf, StandardOpenOption.CREATE);
    }

    @Test
    @Ignore
    public void testToPNGResult() throws Exception
    {
        String svg = Files.readAllLines(new File("C:\\Temp\\stocks2\\5c75925b2a15c68470e7c0ba.svg").toPath(), StandardCharsets.UTF_8)
                          .stream()
                          .collect(Collectors.joining());
        SVGPDFUtils.toPNGResult(svg)
                   .writeTo(new File("C:/Temp/stocks2/atest.png"));
    }
}
