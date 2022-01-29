const {SlashCommandBuilder} = require("@discordjs/builders");
const {capitalizeFirstLetters} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('embark')
        .setDescription('Lets an army/trader/character or armed company embark on a ship.')
        .addStringOption(option =>
            option.setName('passenger-name')
                .setDescription('The army\'s name')
                .setRequired(true))
        .addStringOption(option =>
            option.setName('claimbuild-name')
                .setDescription('The claimbuild\'s name')
                .setRequired(true))
        .addIntegerOption(option =>
            option.setName('region')
                .setDescription('The siege equipment chosen')
                .setRequired(true)),
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());
        const claimbuild=capitalizeFirstLetters(interaction.options.getString('claimbuild-name').toLowerCase());
        const region=interaction.options.getInteger('region');
        await interaction.reply(`${name} has picked up siege equipment (${siege}) at ${claimbuild}.`);
    },
};