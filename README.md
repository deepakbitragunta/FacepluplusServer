FacepluplusServer
=================

This is the server module for Face recognition project. There are two main functions of this module. 

1. Training phase: Given a direcotry of images of respective persons placed in a directory and named with the user id of the person. It will automatically train all the images.

2. Recoginition phase: Given a JSON object, it will send the image to Faceplusplus server for recognition and displays the formatted JSON result. Input JSON : {{picurl:"http:\\yourimageurl"}};
