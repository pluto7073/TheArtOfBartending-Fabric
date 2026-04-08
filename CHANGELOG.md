
### We're Finally at 1.0!

## Additions
- Added vine frames and vine-based plants
- Added green and red grapes
- Added the ability to change initial fermentation durations via config

## Changes
- Default year length for fermentation is 3 minecraft days (72000 ticks)
- Renamed some steps:
  - Boiling is now called Fermentation, Initial Fermentation, or Primary Fermentation
  - Fermentation (Barrel) is now called Barrel Aging
- Edited some recipes
  - Both red and white wine are made using grapes now
  - All wine recipes require 3 stacks of the primary item

---

## Changes
- Updated to Create 6.x

---

## Changes
- Updated for PDAPI 0.4.x and Chemicals 2.x
- Configuration is now managed through a config file rather than gamerules
  - You will have to reset values for doBlackout and yearLengthTicks
  - This config can be managed through ModMenu with Cloth Config installed
  - This config is also joined with the PDAPI config and other Pluto's Drinks Mods

---

## Additions
- Added Everclear
  - Made with 2 stacks of wheat, boiled for ~3 minutes, and distilled 6 times

---

## Changes
- Updated to PDAPI 0.3.12
- Sipping is back via pdapi
- Base Drink Volumes:
  - Mixed Drink: 0oz
  - Wine, Mead, Vermouth, & Absinthe - 5oz
  - Beer - 12oz
- Drink Addition Volumes: 
  - All Shots - 1.5oz
  - Orange - 3oz
  - Lime - 0.5oz
  - Sweet Berries - 0.25oz

---

## Additions
- Two new glasses, the Tall and Short glass
  - The tall glass is the same that beer appeared to have been in
  - The short glass is the kind the rich people in soap operas pour whiskey
- When making a Specialty drink from a Mixed Drink in a datapack, use the `"bartending:glass_base"` base to specify the specific type of glass needed for that drink.  This takes the argument `"glass"` which is the glass's ID  Can be one of:
  - `minecraft:glass_bottle`
  - `bartending:tall_glass`
  - `bartending:short_glass`
  - `bartending:wine_glass`
  - `bartending:cocktail_glass`
- Two Tea based cocktails when TeaTime is installed
  - Earl Grey Old Fashioned (Requires Fruitfulfun and Tea Time): A combination of Whiskey and Earl Grey tea with orange garnish
  - Bourbon Sweet Tea (Requires Tea Time): Sweet black tea with a hint of Whiskey

## Changes
- Temporarily removed the Sipping feature, will be re-added later
- Tall glasses are now used to pour beer into
- Any empty glass can now be used as a base for a Mixed Drink and Specialty Drinks based on Mixed Drinks
  - This does not include drinks like the Mimosa or Death in the Afternoon as those are based on the Champagne item
- Tweaked some specialty drinks to fit with PDAPI updates

## Fixes
- Appletini recipe once again requires sugar and an apple
- Sweet berries addition now has a translation

---

## Fixes
- Dark beer's glass is now the Glass Bottle, instead of Glass of Beer
- Fixed Workstation recipes for Apple and Cocoa Bean additions

---

## Additions
- Champagne!
  - Made by fermenting a bottle of White Wine for at least a day, then placing it in the bottler
- Various New Cocktails:
  - Mimosa (Requires Fruitful Fun): Add an Orange to a class of Champagne
  - Death in the Afternoon: Add a shot of Absinthe to a glass of Champagne
  - Old Fashioned: Sugar + Whiskey Shot + Sweet Berries for Garnish
  - Kamikaze (Requires Fruitful Fun): Vodka + Orange Liqueur + Lime
  - Manhattan: 2 Shots of Whiskey + Sweet Vermouth
  - Vodka Martini: 3 shots of Vodka + Dry Vermouth

## Changes
- Updated structure of drinks/additions for new PDAPI syntax (0.3.3+)
- ABV of Orange Liqueur has been raised to 40% (from 25%)

## Fixes
- Specialty Drinks based off of Mixed Drinks now give the player an empty cocktail glass when drank

---

## Additions
- A new chemical, Absorbed Alcohol, this chemical is now what determines effects

## Changes
- Removed the buckets and distinct fluids for each alcohol type, moving them to a single virtual fluid with NBT data
- Alcohol is no longer absorbed immediately when drinking, and now it takes some time for the player to feel the effects

## Fixes
- Coffee Liqueur once again has a model and recipes

---

## Changes
- Updated to PDAPI 0.3.x, had to shuffle some things

## Fixes
- Alcoholic drinks based on Dark Beer or Apple Mead now correctly show the amount of alcohol

---

## Additions
- `/drink alcohol set ...` command to set the BAC of a player

## Changes
- Blackouts are now controlled by the `doBlackout` gamerule, defaults to true

---