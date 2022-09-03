const {SlashCommandBuilder} = require('@discordjs/builders');
const {capitalizeFirstLetters} = require("../utils/utilities");
const {availableFactions, serverIP, serverPort} = require("../configs/config.json");
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
        const faction = capitalizeFirstLetters(interaction.options.getString('faction-name').toLowerCase());
        log.debug(`Called register with data ign=${ign}, faction=${faction}`)

        log.debug("Checking if faction is valid")
        if (!availableFactions.includes(faction)) {
            log.warn(`Faction ${faction} is not a valid faction`)
            const replyEmbed = new MessageEmbed()
                .setTitle(`Error while linking Discord-Account and Roleplay-System`)
                .setColor('RED')
                .setDescription(`'${faction}' is not a valid faction.`)
                .setFields(
                    {name: "Available Factions", value: availableFactions.join(', '), inline: false}
                )
                .setTimestamp()
            await interaction.editReply({embeds: [replyEmbed], ephemeral: false});
        } else {
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

        }
    },
};