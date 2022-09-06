const {SlashCommandBuilder} = require("@discordjs/builders");
const {capitalizeFirstLetters} = require("../utils/utilities");
const {PICK_SIEGE} = require('../configs/embed_thumbnails.json');
const axios = require("axios");
const {serverIP, serverPort} = require("../configs/config.json");
const {MessageEmbed} = require("discord.js");
const {getLogger} = require("log4js");
const log = getLogger();

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

        log.debug(`Called pick-siege with data armyName=${armyName}, claimbuildName=${claimbuildName}, siege=${siege}`)

        const data = {
            executorDiscordId: interaction.member.id,
            armyName: armyName,
            claimbuildName: claimbuildName,
            siege: siege
        }

        log.debug("Sending request to server")
        axios.patch(`http://${serverIP}:${serverPort}/api/army/pick-siege`, data)
            .then(async function (response) {
                log.debug("Received response")
                const responseData = response.data;

                log.debug("Building embed")
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
                log.debug("Sending reply to discord")
                await interaction.editReply({embeds: [replyEmbed]});
                log.info("Finished pick-siege command")
            })
            .catch(async function(error) {
                log.warn(`Received backend error: ${error.response.data.message}`)
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while trying to pick siege!")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]})
            })


    },
};