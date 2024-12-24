Note: We strongly advise you to put the .jar file of the application in a private folder for it, as
the application may create different folders and files when running.

##############################      HOW TO USE      ##############################

     After the first run, you have all the necessary files and folders created.

     After the first run you should have the following folders:

                 Logs - While the program is running, it will create a log in this folder containing debug messages or errors, if any.
                 Include - folder from which the image files will be taken.
                 Output - folder where the schematics will be saved.


     You will also have a file called 'settings.json'. It stores various settings that will be used when creating the schematic.

     To make a schematic, place the image file in the 'Include' folder and run the application. When finished, a nbt file with the
     same name as the image will appear in the 'Output' folder. The application can also create several schematics if several images
     are placed in the 'Include' folder.

##################################################################################

###############################      SETTINGS      ###############################

     You can customize the various settings to be used at startup using the settings.json file.


     All settings are in the settings.json file:

                 inFolder - name of the folder from which the image will be taken to create the scheme (default is "Include").
                 outFolder - name of the folder to which the schematic file will be saved (default is "Output").
                 DataVersion - Data version of the contents of the schematic (Best left untouched) (default is 3700)
                 SizeScale - A multiplier of the size of the schematic with respect to the input image. (default is 1.0)
                 DisableSaveAirData - Disables the saving of air block position data, thereby reducing the file size. (default is true)
                 MaxThreads - The maximum number of threads that can be created.
                               The more threads, the more schematics can be processed simultaneously.
                                It is worth considering that this also affects the CPU consumption of the program. (default is 4)
                 palettes - list of palettes that will be used to create schematics (default is the obsidian palette).


                 Palette structure:
                 {
                       "BlockName": "",
                       "Min Red": 0/255,
                       "Min Green": 0/255,
                       "Min Blue": 0/255,
                       "Max Red": 0/255,
                       "Max Green": 0/255,
                       "Max Blue": 0/255,
                       "Priority": 0-infinity
                 }

                 "BlockName" - the name of the minecraft block (e.g. minecraft:cobblestone).
                 "Priority" - Palette Prioritization. If several palettes meet the conditions, the one with the higher priority or the
                              most recent one (if their priorities are the same) will be selected.

##################################################################################