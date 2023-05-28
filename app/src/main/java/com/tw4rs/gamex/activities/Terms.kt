/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType
import com.tw4rs.gamex.R

class Terms : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSlide(
            AppIntroFragment.createInstance(
                "SAQUES",
                "Você pode solicitar quantos saques quiser por mês sem limitação",
                R.drawable.ic_baseline_redeem_24, R.color.colorPrimary
            )
        )
        addSlide(
            AppIntroFragment.createInstance(
                "PAGAMENTO",
                "SÃO REALIZADOS OS PAGAMENTOS ENTRE 1 DIA ATÉ O PRÓXIMO DIA 20",
                R.drawable.baseline_monetization_on_black_24dp, R.color.colorPrimary
            )
        )
        addSlide(
            AppIntroFragment.createInstance(
                "BLOQUEIO CONTA",
                "NÃO TEM AÇÃO HUMANA NO BLOQUEIO , É O PROPRIO SISTEMA DO APP QUE BLOQUEIA",
                R.drawable.gamex, R.color.colorPrimary
            )
        )
        addSlide(
            AppIntroFragment.createInstance(
                "BLOQUEIO CONTA - MOTIVOS",
                "VPN , USAR OPÇÃO DE DESENVOLVEDOR , ROOT , PASSAR DO LIMITE DE PONTUAÇÃO ",
                R.drawable.gamex, R.color.colorPrimary
            )
        )
        addSlide(
            AppIntroFragment.createInstance(
                "AJUDA",
                "PEDIDO DE AJUDA POR EMAIL É MAIS DEMORADO , USE O TELEGRAM ",
                R.drawable.gamex, R.color.colorPrimary
            )
        )
        addSlide(
            AppIntroFragment.createInstance(
                "OBSERVAÇÃO",
                "FEZ O PEDIDO COM A CHAVE PIX ERRADA , IREMOS ENVIAR NA MESMA NÃO HÁ TROCA APÓS O PEDIDO" +
                        "APARECEU STATUS NÃO ENCONTRADO / INVÁLIDO / FRAUDE -> NÃO TERÁ O REENVIO DO MESMO " +
                        "RECEBEU PORÉM NÃO LEMBRAVA E FEZ O ESTORNO NÃO VAMOS PAGAR ",
                R.drawable.gamex, R.color.colorPrimary
            )
        )
        addSlide(
            AppIntroFragment.createInstance(
                "OBSERVAÇÃO",
                "PODE APARECER PAGO E NÃO TER CAIDO , POIS ENVIAMOS EM LOTE DE UMA VEZ PORTANTO JÁ ESTÁ PROGRAMADO PARA ENVIAR , AGUARDE",
                R.drawable.gamex, R.color.colorPrimary
            )
        )
        addSlide(
            AppIntroFragment.createInstance(
                "AVISO",
                "NÃO USOU O APP EM 30 DIAS SUA CONTA SERÁ REMOVIDA DO SISTEMA",
                R.drawable.gamex, R.color.colorPrimary
            )
        )
        // Fade Transition
        setTransformer(AppIntroPageTransformerType.Fade)

        // Show/hide status bar
        showStatusBar(true)

        //Speed up or down scrolling
        setScrollDurationFactor(2)

        //Enable the color "fade" animation between two slides (make sure the slide implements SlideBackgroundColorHolder)
        isColorTransitionsEnabled = true

        //Prevent the back button from exiting the slides
        isSystemBackButtonLocked = true

        //Activate wizard mode (Some aesthetic changes)
        isWizardMode = true

        //Show/hide skip button
        isSkipButtonEnabled = false

        //Enable immersive mode (no status and nav bar)
        setImmersiveMode()

        //Enable/disable page indicators
        isIndicatorEnabled = true

        //Dhow/hide ALL buttons
        isButtonsEnabled = true
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        finish()
    }
}