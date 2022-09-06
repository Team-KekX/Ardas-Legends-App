const {SlashCommandBuilder} = require('@discordjs/builders');
const {capitalizeFirstLetters} = require("../utils/utilities");
const {serverIP, serverPort} = require("../configs/config.json");
const {MessageEmbed} = require("discord.js");
const {REGISTER} = require("../configs/embed_thumbnails.json");
const axios = require("axios");
const log = require("log4js").getLogger();

// Needs to be further implemented.
// Reaction counting is currently not implemented.
module.exports = {
    data: new SlashCommandBuilder()
        .setName('register')
        .setDescription('Register in the roleplay system')
        .addStringOption(option =>
            option.setName('ign')
                .setDescription('Your minecraft in-game name (IGN)')
                .setRequired(true))
        .addStringOption(option =>
            option.setName('faction-name')
                .setDescription('The faction you want to join')
                .setRequired(true)),
    async execute(interaction) {

        const ign = interaction.options.getString('ign');
        const faction = interaction.options.getString('faction-name');
        log.debug(`Called register with data ign=${ign}, faction=${faction}`)


        // send to server
        const data = {
            ign: ign,
            discordID: interaction.member.id,
            faction: faction
        }

        log.debug("Sending request to server")
        axios.post('http://'+serverIP+':'+serverPort+'/api/player/create', data)
            .then(async function (response) {
                log.debug("Received response")
                // The request and data is successful.
                log.debug("Building embed")
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Register`)
                    .setColor('GREEN')
                    .setDescription(`You were successfully registered as ${ign} in the faction ${faction}.`)
                    .setThumbnail(REGISTER)
                    .setTimestamp()
                log.debug("Sending reply to discord")
                await interaction.editReply({embeds: [replyEmbed], ephemeral: false});
                log.info("Finished register command")
            })
            .catch(async function (error) {
                log.warn(`Received backend error: ${error.response.data.message}`)
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Error while linking Discord-Account and Roleplay-System`)
                    .setColor('RED')
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]});
            })


    },
};