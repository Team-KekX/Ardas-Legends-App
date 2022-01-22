
module.exports = {
    async execute(interaction) {
        let name=interaction.options.getString('armed-company-name').toLowerCase();
        let army=interaction.options.getString('army-name').toLowerCase();
        let trader=interaction.options.getString('trader-name').toLowerCase();
        let character=interaction.options.getString('character-name').toLowerCase();
        //split the above strings into arrays of strings
        //whenever a blank space is encountered

        const arr_name = name.split(" ");
        const arr_army = army.split(" ");
        const arr_trader = trader.split(" ");
        const arr_character = character.split(" ");

        //loop through each element of the array and capitalize the first letter.

        for (let i = 0; i < arr_name.length; i++) {
            arr_name[i] = arr_name[i].charAt(0).toUpperCase() + arr_name[i].slice(1);
        }
        for (let i = 0; i < arr_army.length; i++) {
            arr_army[i] = arr_army[i].charAt(0).toUpperCase() + arr_army[i].slice(1);
        }
        for (let i = 0; i < arr_trader.length; i++) {
            arr_trader[i] = arr_trader[i].charAt(0).toUpperCase() + arr_trader[i].slice(1);
        }
        for (let i = 0; i < arr_character.length; i++) {
            arr_character[i] = arr_character[i].charAt(0).toUpperCase() + arr_character[i].slice(1);
        }

        //Join all the elements of the array back into a string
        //using a blankspace as a separator
        name = arr_name.join(" ");
        army = arr_army.join(" ");
        trader = arr_trader.join(" ");
        character = arr_character.join(" ");
        await interaction.reply(`The armed company ${name} comprised of the army ${army} and trading company ${trader},
         has been created and bound to ${character}.`);
    },
};