
module.exports = {
    async execute(interaction) {
        let name=interaction.options.getString('army-name').toLowerCase();
        let character=interaction.options.getString('character-name').toLowerCase();
        //split the above strings into arrays of strings
        //whenever a blank space is encountered

        const arr_name = name.split(" ");
        const arr_character = character.split(" ");

        //loop through each element of the array and capitalize the first letter.

        for (let i = 0; i < arr_name.length; i++) {
            arr_name[i] = arr_name[i].charAt(0).toUpperCase() + arr_name[i].slice(1);
        }
        for (let i = 0; i < arr_character.length; i++) {
            arr_character[i] = arr_character[i].charAt(0).toUpperCase() + arr_character[i].slice(1);
        }

        //Join all the elements of the array back into a string
        //using a blankspace as a separator
        name = arr_name.join(" ");
        character = arr_character.join(" ");
        await interaction.reply(`${character} has been bound to the army ${name}`);
    },
};