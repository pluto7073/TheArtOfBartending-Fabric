
### We're Finally at 1.0!

## Additions
- Added vine frames and vine-based plants
- Grapes!
- Added the ability to change initial fermentation durations via config

## Changes
- Default year length for fermentation is 3 minecraft days (72000 ticks)
- All empty glass bottles can be smelted to receive 1 glass block, defined in `#bartending:empty_glass_bottles`
- Renamed some steps:
  - Boiling is now called Fermentation, Initial Fermentation, or Primary Fermentation
  - Fermentation (Barrel) is now called Barrel Aging or Barrel Fermentation
- Renamed Everclear to generic "Grain Alcohol"
- Edited some recipes
  - Both red and white wine are made using grapes now
  - All wine recipes require 3 stacks of the primary item
  - Beer now needs to be barrel aged for less time
    - Beer now at least 1/2 a "year" with a 1/4 "year" lower limit (default 1.5 days, min ~1 day)
    - Wheat Beer now at least 1/3 a "year" with a 1/6 "year" lower limit (default 1 day, min 1/2 day)
    - Dark Beer now at least 2/3 a "year" with a 1/3 "year" lower limit (default 2 days, min 1 day)
  - Changed amount of wheat required for dark beer, from (20 → 28) to (26 → 38) 
  - Changed length of initial fermentation of various recipes:
    - Beer: now 1-3 minecraft days
    - Wheat Beer: now 1-2 minecraft days
    - Dark Beer: now 2-4 minecraft days
    - Mead & Apple Mead: now 4-6 minecraft days
    - Normal Wines: now around 1 minecraft day
    - Nether Wines: now 1-3 minecraft days
    - Gin: now 1-2 days
    - Tequila: now ~1 day
  - Changed amount of honey required for mead and apple mead: 4 to 8
  - Tequila only needs to be barrel aged for around 1 year (default 3 days, min 1 day)
  - Tequila also needs to be distilled before being barrel aged
  - Changed Orange Liqueur recipe
    - Starts by fermenting beets for ~1 day
    - Then distilling 2 times
    - Then adding ~10 oranges to the bottle using the drink workstation
    - Then redistilled 3 times
  - Changed Apple Liqueur recipe
    - Start with a base of Vodka and add 64 of any apple (Create Honeyed Apples included) (Must all be same apple)
    - Distill this 3 times
    - Age in a Cherry or Acacia Barrel for ~6 years (default 18 days, min 12 days)
  - Changed Vodka Recipe
    - Can now be made with Wheat or Skinned Grapes in addition to potatoes
    - Fermented for ~1 day
    - Distilled 3 (2-4) times
    - Now used as a base for many liqueur recipes
  - Added recipes for both Vermouths without create
    - Take a bottle of wine (red for sweet, white for dry)
    - Add ~5 shots of vodka
    - If sweet: add 3 sugar OR 3 honey bottles
    - Add 3 of Hanging Roots, Flowers, Seeds, OR Tree bark with Farmer's Delight
    - Age in any barrel for at least half a year
    - Rebottle in a wine bottle
  - Changed Recipe for Coffee Liqueur with (and without) Create
    - In create basin, now 16 coffee beans are required
    - Without Create: now start with a bottle of Vodka OR Rum and add 3 sugar and 16 coffee beans, age in any barrel for about 6 minutes then rebottle
    - Lowered Coffee liqueur to 35% ABV (from 40%)
  - Grain alcohol can now be made with potatoes and skinned grapes, distinct from vodka by distillation count
  - Grain alcohol is now initially fermented for 1-2 days, and distilled 6 (5 - 7) times
  - Grain alcohol can now be watered down into vodka using 2 water bottles and rebottling
  - Absinthe is now made with a base of Grain Alcohol, then by adding the normal ingredients to the bottle and by distilling ~6 times
  - Rum is now fermented for ~2-5 minutes, then distilled twice, then aged for at least one year in any oak barrel
  - Added support for PaleGardenBackport's Pale Oak to be used as a barrel and count in recipes that require oak barrels
  - The items with create specific recipes can now be crafted using their non-create recipes even if create is installed (create recipes are just more efficient)
  - See [the wiki](https://www.github.com/pluto7073/PlutosDrinksAPI/wiki) or use [REI](https://modrinth.com/mod/rei) for full recipes
- Changed how barrel fermentation works:
  - Now, each alcoholic drink that requires barrel aging has a minimum amount of "years" required but can be aged as long as desired, additionally there is a lower padding limit provided which will produce less than the normal amount of alcohol
  - (Alcohol content stops increasing after a certain length)

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