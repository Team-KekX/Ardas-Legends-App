const {SlashCommandBuilder} = require("@discordjs/builders");
const {capitalizeFirstLetters} = require("../utils/utilities");
const {PICK_SIEGE} = require('../configs/embed_thumbnails.json');
const axios = require("axios");
const {serverIP, serverPort} = require("../configs/config.json");
const {MessageEmbed} = require("discord.js");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('pick-siege')
        .setDescription('Pick up siege equipment with an army')
        .addStringOption(option =>
            option.setName('army-name')
                .setDescription('The army\'s name')
                .setRequired(true))
        .addStringOption(option =>
            option.setName('claimbuild-name')
                .setDescription('The name of the claimbuild to pick up siege from')
                .setRequired(true))
        .addStringOption(option =>
            option.setName('siege-name')
                .setDescription('The siege equipment chosen')
                .setRequired(true)),
    async execute(interaction) {
        const armyName = capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());
        const claimbuildName = capitalizeFirstLetters(interaction.options.getString('claimbuild-name').toLowerCase());
        const siege = capitalizeFirstLetters(interaction.options.getString('siege-name').toLowerCase());

        const data = {
            executorDiscordId: interaction.member.id,
            armyName: armyName,
            claimbuildName: claimbuildName,
            siege: siege
        }

        axios.patch(`http://${serverIP}:${serverPort}/api/army/pick-siege`, data)
            .then(async function (response) {
                const responseData = response.data;

                const replyEmbed = new MessageEmbed()
                    .setTitle(`Army '${armyName}' has picked up siege equipment!'`)
                    .setColor('GREEN')
                    .setDescription(`The ${armyName} has picked up new siege equipment (${siege}).`)
                    .setThumbnail(PICK_SIEGE)
                    .setFields(
                        {name: "From claimbuild", value: claimbuildName, inline: true},
                        {name: "Current equipment", value: responseData.sieges.join(", "), inline: true}
                    )
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while trying to pick siege!")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]})
            })


    },
};