# Multimedia-Image-Quantizer
An image manipulator and seacher done for University using Java and JavaFX. 

## Overview
The project aims to compress images using Image Quantization and Indexed Image Storing while also providing basic Image manipulation such as Resizing and Cropping.
Another aim is to provide an image searcher to find similar images using different criteria like: color prevelance, size and date.

## Implentation
The language is Java with JavaFX to create the GUI.
We are using a model-View arch so that all the algorithms and computations are independent of the GUI and user oriented stuff.

* The Algorithms folder include the 3 quantization algorithms with all related functions.
* While the rest of the Image folder include all classes and functions used in writing and handling the images but are not related to the algorithms.
* All the other files and general and don't need a folder.

## Technicalities
in order to achieve best results in the search we converted from RGB to [CIELAB color system](https://en.wikipedia.org/wiki/CIELAB_color_space).

all the indexed images have the header 888 as the very first bits in the file.

## Collabrators
* [Redwan Alloush](https://github.com/RedWn)
* * The algorithms, the search and writing the indexed images.
* [Hasan Mothaffar](https://github.com/HasanMothaffar)
* * overall QA and testing as well as GUI work
* [Iyad Alanssary](https://github.com/IyadAlanssary)
* * color histograms and GUI work
* [Anton Dirani](https://github.com/AntonDirani)
* * GUI features such as cropping, resizeing, etc.
