const {SlashCommandBuilder} = require("@discordjs/builders");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('pick-siege')
        .setDescription('Pick a siege equipment with an army or armed company')
        .addStringOption(option =>
            option.setName('army-name')
                .setDescription('The army\'s name')
                .setRequired(true))
        .addStringOption(option =>
            option.setName('claimbuild-name')
                .setDescription('The claimbuild\'s name')
                .setRequired(true))
        .addStringOption(option =>
            option.setName('siege-name')
                .setDescription('The siege equipment chosen')
                .setRequired(true)),
    async execute(interaction) {
        let name=interaction.options.getString('army-name').toLowerCase();
        let claimbuild=interaction.options.getString('claimbuild-name').toLowerCase();
        let siege=interaction.options.getString('siege-name').toLowerCase();

        //split the above strings into arrays of strings
        //whenever a blank space is encountered

        const arr_name = name.split(" ");
        const arr_claimbuild = claimbuild.split(" ");
        const arr_siege = siege.split(" ");

        //loop through each element of the array and capitalize the first letter.


        for (let i = 0; i < arr_name.length; i++) {
            arr_name[i] = arr_name[i].charAt(0).toUpperCase() + arr_name[i].slice(1);
        }
        for (let i = 0; i < arr_claimbuild.length; i++) {
            arr_claimbuild[i] = arr_claimbuild[i].charAt(0).toUpperCase() + arr_claimbuild[i].slice(1);
        }
        for (let i = 0; i < arr_siege.length; i++) {
            arr_siege[i] = arr_siege[i].charAt(0).toUpperCase() + arr_siege[i].slice(1);
        }

        //Join all the elements of the array back into a string
        //using a blankspace as a separator
        name = arr_name.join(" ");
        claimbuild = arr_claimbuild.join(" ");
        siege = arr_siege.join(" ");
        await interaction.reply(`${name} has picked up siege equipment (${siege}) at ${claimbuild}.`);
    },
};