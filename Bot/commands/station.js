const { SlashCommandBuilder } = require('@discordjs/builders');

// Needs to be further implemented.
// Reaction counting is currently not implemented.
module.exports = {
    data: new SlashCommandBuilder()
        .setName('station')
        .setDescription('Station an army in a claimbuild')
        .addStringOption(option =>
            option.setName('army-name')
                .setDescription('Your army\'s name')
                .setRequired(true))
        .addStringOption(option =>
            option.setName('claimbuild-name')
                .setDescription('The claimbuild\'s name')
                .setRequired(true)),
    async execute(interaction) {
        let name=interaction.options.getString('army-name').toLowerCase();
        let claimbuild=interaction.options.getString('claimbuild-name').toLowerCase();

        //split the above strings into arrays of strings
        //whenever a blank space is encountered

        const arr_name = name.split(" ");
        const arr_claimbuild = claimbuild.split(" ");

        //loop through each element of the array and capitalize the first letter.


        for (let i = 0; i < arr_name.length; i++) {
            arr_name[i] = arr_name[i].charAt(0).toUpperCase() + arr_name[i].slice(1);
        }
        for (let i = 0; i < arr_claimbuild.length; i++) {
            arr_claimbuild[i] = arr_claimbuild[i].charAt(0).toUpperCase() + arr_claimbuild[i].slice(1);
        }

        //Join all the elements of the array back into a string
        //using a blankspace as a separator
        name = arr_name.join(" ");
        claimbuild = arr_claimbuild.join(" ");
        await interaction.reply(`${name} is now stationed in ${claimbuild}.`);
    },
};