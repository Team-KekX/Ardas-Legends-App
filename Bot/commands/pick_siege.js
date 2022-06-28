const {SlashCommandBuilder} = require("@discordjs/builders");
const {capitalizeFirstLetters} = require("../utils/utilities");
const {PICK_SIEGE} = require('../../configs/embed_thumbnails.json');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('pick-siege')
        .setDMPermission(false)
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
        const name=capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());
        const claimbuild=capitalizeFirstLetters(interaction.options.getString('claimbuild-name').toLowerCase());
        const siege=capitalizeFirstLetters(interaction.options.getString('siege-name').toLowerCase());
        const replyEmbed = new MessageEmbed()
                                .setTitle(`Pick up siege equipment`)
                                .setColor('RED')
                                .setDescription(`${name} has picked up siege equipment (${siege}) at ${claimbuild}.`)
                                .setThumbnail(PICK_SIEGE)
                                .setTimestamp()
        await interaction.reply({embeds: [replyEmbed]});
    },
};